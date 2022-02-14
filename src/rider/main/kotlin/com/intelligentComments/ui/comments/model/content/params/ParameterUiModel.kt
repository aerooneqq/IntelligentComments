package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.ParameterSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.segments.ParameterRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class ParameterUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  parameter: ParameterSegment
) : AbstractParameterUiModel(project, parent, parameter) {
  override fun createRenderer(): Renderer {
    return ParameterRenderer(this)
  }
}