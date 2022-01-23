package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.domain.core.CommentBase
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project

abstract class CommentUiModelBase(
  val comment: CommentBase,
  project: Project,
  val editor: Editor
) : UiInteractionModelBase(project, null), RootUiModel, ModelWithContent {
  private val controller = project.getComponent(RiderCommentsController::class.java)

  override fun handleClick(e: EditorMouseEvent): Boolean {
    controller.toggleModeChange(comment.commentIdentifier, e.editor as EditorImpl)
    return true
  }
}