package com.intelligentcomments.ui.comments.model

import com.intelligentcomments.core.comments.RiderCommentsController
import com.intelligentcomments.core.comments.states.RiderCommentsStateManager
import com.intelligentcomments.core.domain.core.CommentBase
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

abstract class CommentUiModelBase(
  val comment: CommentBase,
  project: Project,
  val editor: Editor
) : UiInteractionModelBase(project, null), RootUiModel, ModelWithContent {
  protected val controller: RiderCommentsController = project.getComponent(RiderCommentsController::class.java)
  protected val commentsStateManager: RiderCommentsStateManager = project.getComponent(RiderCommentsStateManager::class.java)

  override fun createRenderer(): Renderer = renderer
}