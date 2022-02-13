package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger

abstract class CommentUiModelBase(
  val comment: CommentBase,
  project: Project,
  val editor: Editor
) : UiInteractionModelBase(project, null), RootUiModel, ModelWithContent {
  companion object {
    private val logger = getLogger<CommentUiModelBase>()
  }

  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val commentsStateManager = project.getComponent(RiderCommentsStateManager::class.java)

  override fun handleClick(e: EditorMouseEvent): Boolean {
    val currentState = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier)
    if (currentState == null) {
      logger.logAssertion("Failed to get comment's state for ${comment.commentIdentifier}")
      return false
    }

    if (currentState.displayKind == CommentsDisplayKind.Render) return false

    controller.toggleModeChange(comment.commentIdentifier, e.editor as EditorImpl)
    return true
  }
}