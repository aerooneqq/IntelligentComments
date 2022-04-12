package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

abstract class ContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
) : UiInteractionModelBase(project, parent)