package com.intelligentcomments.core.comments.states

import com.intelligentcomments.core.changes.Change
import com.intelligentcomments.core.changes.ChangeListener
import com.intelligentcomments.core.changes.ChangeManager
import com.intelligentcomments.core.changes.SettingsChange
import com.intelligentcomments.core.comments.storages.CommentsIdentifierStorage
import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intelligentcomments.core.settings.CommentsDisplayKind
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.application
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rdclient.document.textControlId
import com.jetbrains.rdclient.editors.getPsiFile
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.ideaInterop.find.scopes.RiderSolutionScope
import kotlin.io.path.Path

@State(
  name = "SolutionCommentsState",
  storages = [Storage("CommentsStates.xml")]
)
class RiderCommentsStateManager(
  project: Project
) : LifetimedProjectComponent(project), PersistentStateComponent<SolutionCommentsState>, ChangeListener {
  private val logger = Logger.getInstance(RiderCommentsStateManager::class.java)
  private val settingsProvider = RiderIntelligentCommentsSettingsProvider.getInstance()
  private val loadedStates: MutableMap<EditorId, MutableMap<LoadedCommentIdentifier, CommentState>> = HashMap()
  private val states: MutableMap<EditorId, CommentsIdentifierStorage<CommentState>> = HashMap()


  init {
    application.invokeLater {
      ChangeManager.getInstance().addListener(componentLifetime, this)
    }
  }


  override fun handleChange(change: Change) {
    application.assertIsDispatchThread()

    if (change is SettingsChange) {
      val displayKindChange = change.changes[settingsProvider.commentsDisplayKind]
      var newValue = displayKindChange?.newValue as? CommentsDisplayKind

      val renderInDecompiledChange = change.changes[settingsProvider.renderCommentsOnlyInDecompiledSources]
      if (renderInDecompiledChange != null) {
        newValue = newValue ?: settingsProvider.commentsDisplayKind.value
      }

      if (newValue != null) {
        application.invokeLater {
          updateAllVisibleStates { _, editorId ->
            return@updateAllVisibleStates adjustFinalStateWithSettings(newValue, editorId)
          }
        }
      }
    }
  }

  private fun updateAllVisibleStates(newDisplayKindCalculator: (CommentsDisplayKind, EditorId) -> CommentsDisplayKind) {
    for ((editorId, editorStates) in states) {
      for ((_, state) in editorStates.getAllKeysAndValues()) {
        state.setDisplayKind(newDisplayKindCalculator(state.displayKind, editorId))
      }
    }
  }

  fun clearState(editor: Editor, commentIdentifier: CommentIdentifier) {
    application.assertIsDispatchThread()
    val editorComments = states[editor.getEditorId()] ?: return
    editorComments.remove(commentIdentifier)
  }

  fun restoreOrCreateCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState {
    application.assertIsDispatchThread()

    val editorId = editor.getEditorId() ?: return CommentState.defaultInstance
    val editorCommentsStates = states.getOrCreate(editorId) { CommentsIdentifierStorage() }
    val existingState = editorCommentsStates.getWithAdditionalSearch(commentIdentifier)
    if (existingState != null) {
      return adjustCommentStateBasedOnCaretPosition(existingState, editor, commentIdentifier)
    }

    val persistentState = getPersistentState(editorId, commentIdentifier)
    return editorCommentsStates.getOrCreate(commentIdentifier) {
      val state = if (persistentState != null) {
        adjustCommentStateBasedOnCaretPosition(persistentState, editor, commentIdentifier)
      } else {
        val settingValue = settingsProvider.commentsDisplayKind.value
        adjustCommentStateBasedOnCaretPosition(CommentState(settingValue), editor, commentIdentifier)
      }

      state.setDisplayKind(adjustFinalStateWithSettings(state.displayKind, editor))
      return@getOrCreate state
    }
  }

  private fun adjustCommentStateBasedOnCaretPosition(
    state: CommentState,
    editor: Editor,
    identifier: CommentIdentifier
  ): CommentState {
    val adjustedKind = if (isCaretWithinComment(editor, identifier.rangeMarker)) {
      CommentsDisplayKind.Code
    } else {
      state.displayKind
    }

    return CommentState(adjustedKind)
  }

  private fun isCaretWithinComment(editor: Editor, range: RangeMarker): Boolean {
    val caretOffset = editor.caretModel.offset
    val caretLine = editor.offsetToVisualLine(caretOffset, true)
    val commentStartLine = editor.offsetToVisualLine(range.startOffset, true)
    val commentEndLine = editor.offsetToVisualLine(range.endOffset, true)

    return caretLine in commentStartLine..commentEndLine
  }

  private fun getPersistentState(editorId: EditorId, commentIdentifier: CommentIdentifier): CommentState? {
    val marker = commentIdentifier.rangeMarker
    val id = LoadedCommentIdentifier(commentIdentifier.moniker, marker.startOffset, marker.endOffset)

    return loadedStates[editorId]?.get(id)
  }

  fun getExistingCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState? {
    return tryGetCommentState(editor, commentIdentifier)
  }

  private fun tryGetCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState? {
    val editorId = editor.getEditorId()
    val editorCommentsStates = states[editorId] ?: return null
    return editorCommentsStates.getWithAdditionalSearch(commentIdentifier)
  }

  fun changeDisplayKind(
    editor: Editor,
    commentIdentifier: CommentIdentifier,
    displayKind: CommentsDisplayKind
  ): CommentState? {
    application.assertIsDispatchThread()
    return executeWithCurrentState(editor, commentIdentifier) {
      it.setDisplayKind(adjustFinalStateWithSettings(displayKind, editor))
    }
  }

  private fun executeWithCurrentState(
    editor: Editor,
    commentIdentifier: CommentIdentifier,
    action: (CommentState) -> Unit
  ): CommentState? {
    val existingState = tryGetCommentState(editor, commentIdentifier)
    if (existingState == null) {
      logger.logAssertion("Trying to change render mode of a comment with not registered state ${editor.getEditorId()} $commentIdentifier")
      return null
    }

    action(existingState)
    return existingState
  }

  fun changeDisplayKind(
    editor: Editor,
    commentIdentifier: CommentIdentifier,
    transform: (CommentsDisplayKind) -> CommentsDisplayKind
  ): CommentState? {
    application.assertIsDispatchThread()
    return executeWithCurrentState(editor, commentIdentifier) {
      it.setDisplayKind(adjustFinalStateWithSettings(transform(it.displayKind), editor))
    }
  }

  private fun adjustFinalStateWithSettings(displayKind: CommentsDisplayKind, editor: Editor): CommentsDisplayKind {
    return if (settingsProvider.renderCommentsOnlyInDecompiledSources.value && !isDecompiledEditor(editor)) {
      CommentsDisplayKind.Code
    } else {
      displayKind
    }
  }

  private fun adjustFinalStateWithSettings(displayKind: CommentsDisplayKind, editorId: EditorId): CommentsDisplayKind {
    return if (settingsProvider.renderCommentsOnlyInDecompiledSources.value && !isDecompiledEditor(project, editorId)) {
      CommentsDisplayKind.Code
    } else {
      displayKind
    }
  }

  fun isInRenderMode(editor: Editor, commentIdentifier: CommentIdentifier): Boolean? {
    val state = tryGetCommentState(editor, commentIdentifier)
    if (state == null) {
      logger.logAssertion("Failed to get state for ${editor.getEditorId()} $commentIdentifier")
      return null
    }

    return state.displayKind != CommentsDisplayKind.Code
  }

  override fun getState(): SolutionCommentsState {
    val snapshots = mutableListOf<CommentStateSnapshot>()

    for ((editorId, comments) in states) {
      for ((rangeMarker, state) in comments.getAllKeysAndValues()) {
        snapshots.add(CommentStateSnapshot().apply {
          moniker = editorId.moniker
          tabOrder = editorId.tabOrder
          startOffset = rangeMarker.startOffset
          endOffset = rangeMarker.endOffset
          displayKind = state.displayKind
        })
      }
    }

    return SolutionCommentsState().apply {
      commentStateSnapshots.addAll(snapshots)
    }
  }

  override fun loadState(solutionCommentsState: SolutionCommentsState) {
    loadedStates.clear()
    for (snapshot in solutionCommentsState.commentStateSnapshots) {
      val editorsComments = loadedStates.getOrCreate(EditorId(snapshot.moniker, snapshot.tabOrder)) { HashMap() }
      val id = LoadedCommentIdentifier(snapshot.moniker, snapshot.startOffset, snapshot.endOffset)
      editorsComments[id] = CommentState(snapshot)
    }
  }
}

data class LoadedCommentIdentifier(val moniker: String, val startOffset: Int, val endOffset: Int)

@Tag("solutionCommentsState")
class SolutionCommentsState {
  @Tag("snapshots")
  @XCollection
  val commentStateSnapshots: MutableList<CommentStateSnapshot> = mutableListOf()
}

@Tag("stateSnapshot")
class CommentStateSnapshot {
  @Attribute("moniker") var moniker: String = EditorId.emptyInstance.moniker
  @Attribute("tabOrder") var tabOrder: Int = EditorId.emptyInstance.tabOrder
  @Attribute("startOffset") var startOffset: Int = 0
  @Attribute("endOffset") var endOffset: Int = 0
  @Attribute("displayKind") var displayKind: CommentsDisplayKind = CommentsDisplayKind.Code
  @Attribute("lastRelativeCaretPositionWithinComment") var lastRelativeCaretPositionWithinComment: Int = 0
}

fun canChangeFromCodeToRender(editor: Editor): Boolean {
  val settings = RiderIntelligentCommentsSettingsProvider.getInstance()

  return settings.commentsDisplayKind.value != CommentsDisplayKind.Code &&
    !(settings.renderCommentsOnlyInDecompiledSources.value && !isDecompiledEditor(editor))
}

fun isDecompiledEditor(editor: Editor): Boolean {
  val project = editor.project ?: return false
  val scope = RiderSolutionScope(project, true)
  val document = editor.document
  val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return false
  val virtualFile = psiFile.virtualFile

  return !scope.contains(virtualFile)
}

fun isDecompiledEditor(project: Project, editorId: EditorId): Boolean {
  val file = VirtualFileManager.getInstance().findFileByNioPath(Path(editorId.moniker)) ?: return false
  return !RiderSolutionScope(project, true).contains(file)
}

fun Editor.getEditorId(): EditorId? {
  val psiFile = getPsiFile()

  val id = textControlId
  if (psiFile == null || id == null) {
    getLogger<RiderCommentsStateManager>().logAssertion("Psi file was null for $this")
    return null
  }

  return EditorId(psiFile.virtualFile.path, id.tabIndex)
}
