package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.ParameterSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

class ParameterUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  parameter: ParameterSegment
) : AbstractParameterUiModel(project, parent, parameter)