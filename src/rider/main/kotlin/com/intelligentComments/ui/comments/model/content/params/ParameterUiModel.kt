package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.ParameterSegment
import com.intellij.openapi.project.Project

class ParameterUiModel(
  project: Project,
  parameter: ParameterSegment
) : AbstractParameterUiModel(project, parameter)