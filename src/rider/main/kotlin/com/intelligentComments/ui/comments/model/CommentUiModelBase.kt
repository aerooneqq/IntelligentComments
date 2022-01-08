package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.CommentBase
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

abstract class CommentUiModelBase(
  val comment: CommentBase,
  project: Project,
  val editor: Editor
) : UiInteractionModelBase(project, null), RootUiModel, ModelWithContent