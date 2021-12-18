package com.intelligentComments.core.comments

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

data class CommentState(val isInRenderMode: Boolean) {
  companion object {
    val defaultInstance = CommentState(false)
  }

  fun changeRenderMode() = CommentState(!isInRenderMode)
}

data class EditorId(val moniker: String, val tabOrder: Int = 0) {
  companion object {
    val emptyInstance = EditorId("", 0)
  }
}

@State(
  name = "SolutionCommentsState",
  storages = [Storage("CommentsStates.xml")]
)
class RiderCommentsStateManager(project: Project) : PersistentStateComponent<SolutionCommentsState> {
  private var states: MutableMap<EditorId, MutableMap<CommentIdentifier, CommentState>> = HashMap()
  private val logger = Logger.getInstance(RiderCommentsStateManager::class.java)
  private val settingsProvider = project.service<RiderIntelligentCommentsSettingsProvider>()


  fun getOrCreateCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState {
    application.assertIsDispatchThread()
    val editorId = editor.getEditorId() ?: return CommentState.defaultInstance

    val editorCommentsStates = states.getOrCreate(editorId) { HashMap() }
    return editorCommentsStates.getOrCreate(commentIdentifier) {
      val isInRenderMode = settingsProvider.commentsDisplayKind.value == CommentsDisplayKind.Render
      CommentState(isInRenderMode)
    }
  }

  fun getExistingCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState? {
    return tryGetCommentState(editor, commentIdentifier)
  }

  private fun tryGetCommentState(editor: Editor, commentIdentifier: CommentIdentifier): CommentState? {
    val editorId = editor.getEditorId()
    val editorCommentsStates = states[editorId] ?: return null
    return editorCommentsStates[commentIdentifier]
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

    val newState = existingState.changeRenderMode()
    if (tryUpdateState(editor, commentIdentifier, newState)) {
      return newState
    }

    return null
  }

  private fun tryUpdateState(editor: Editor, commentIdentifier: CommentIdentifier, newState: CommentState): Boolean {
    val editorId = editor.getEditorId()
    val editorsCommentStates = states[editorId]
    if (editorsCommentStates == null) {
      logger.logAssertion("Trying to update state of a comment with not registered document $editorId")
      return false
    }

    val existingState = editorsCommentStates[commentIdentifier]
    if (existingState == null) {
      logger.logAssertion("Trying to update state for a not registered comment $editorId $commentIdentifier")
      return false
    }

    editorsCommentStates[commentIdentifier] = newState
    return true
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
      for ((commentId, state) in comments) {
        snapshots.add(CommentStateSnapshot().apply {
          moniker = editorId.moniker
          tabOrder = editorId.tabOrder
          rangeHash = commentId.commentIdentifier
          isInRenderMode = state.isInRenderMode
        })
      }
    }

    return SolutionCommentsState().apply {
      commentStateSnapshots.addAll(snapshots)
    }
  }

  override fun loadState(solutionCommentsState: SolutionCommentsState) {
    states.clear()
    for (snapshot in solutionCommentsState.commentStateSnapshots) {
      val editorsComments = states.getOrCreate(EditorId(snapshot.moniker, snapshot.tabOrder)) { HashMap() }
      editorsComments[CommentIdentifier(snapshot.moniker, snapshot.rangeHash)] = CommentState(snapshot.isInRenderMode)
    }
  }
}

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
  @Attribute("rangeHash") var rangeHash: Int = 0
  @Tag("isInRenderMode") var isInRenderMode: Boolean = false
}