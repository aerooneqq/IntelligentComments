package com.intelligentComments.core.comments.states

import com.intelligentComments.core.comments.CommentsIdentifierStorage
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rdclient.editors.getPsiFile

@State(
  name = "SolutionCommentsState",
  storages = [Storage("CommentsStates.xml")]
)
class RiderCommentsStateManager(project: Project) : PersistentStateComponent<SolutionCommentsState> {
  private val logger = Logger.getInstance(RiderCommentsStateManager::class.java)
  private val settingsProvider = project.service<RiderIntelligentCommentsSettingsProvider>()
  private val loadedStates: MutableMap<EditorId, MutableMap<LoadedCommentIdentifier, CommentState>> = HashMap()
  private val states: MutableMap<EditorId, CommentsIdentifierStorage<CommentState>> = HashMap()


  fun restoreOrCreateCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState {
    application.assertIsDispatchThread()
    val editorId = editor.getEditorId() ?: return CommentState.defaultInstance

    val persistentState = getPersistentState(editorId, commentIdentifier)

    val editorCommentsStates = states.getOrCreate(editorId) { CommentsIdentifierStorage() }
    val existingState = editorCommentsStates.getWithAdditionalSearch(commentIdentifier)
    if (existingState != null) return existingState

    return editorCommentsStates.getOrCreate(commentIdentifier) {
      if (persistentState != null) {
        persistentState
      } else {
        val isInRenderMode = settingsProvider.commentsDisplayKind.value == CommentsDisplayKind.Render
        CommentState(isInRenderMode)
      }
    }
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

  private fun Editor.getEditorId(): EditorId? {
    val psiFile = getPsiFile()

    if (psiFile == null) {
      logger.logAssertion("Psi file was null for $this")
      return null
    }

    return EditorId(psiFile.virtualFile.path)
  }

  fun changeRenderMode(editor: Editor, commentIdentifier: CommentIdentifier): CommentState? {
    application.assertIsDispatchThread()
    val existingState = tryGetCommentState(editor, commentIdentifier)
    if (existingState == null) {
      logger.logAssertion("Trying to change render mode of a comment with not registered state ${editor.getEditorId()} $commentIdentifier")
      return null
    }

    existingState.changeRenderMode()
    return existingState
  }

  fun isInRenderMode(editor: Editor, commentIdentifier: CommentIdentifier): Boolean? {
    val state = tryGetCommentState(editor, commentIdentifier)
    if (state == null) {
      logger.logAssertion("Failed to get state for ${editor.getEditorId()} $commentIdentifier")
      return null
    }

    return state.isInRenderMode
  }

  override fun getState(): SolutionCommentsState {
    val snapshots = mutableListOf<CommentStateSnapshot>()

    for ((editorId, comments) in states) {
      for ((commentId, state) in comments.getAllKeysAndValues()) {
        snapshots.add(CommentStateSnapshot().apply {
          moniker = editorId.moniker
          tabOrder = editorId.tabOrder
          startOffset = commentId.rangeMarker.startOffset
          endOffset = commentId.rangeMarker.endOffset
          isInRenderMode = state.isInRenderMode
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
  @Attribute("isInRenderMode") var isInRenderMode: Boolean = false
  @Attribute("lastRelativeCaretPositionWithinComment") var lastRelativeCaretPositionWithinComment: Int = 0
}