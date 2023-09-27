package com.intelligentcomments.ui.comments.model.content.params

import com.intelligentcomments.core.domain.core.ParameterSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intellij.openapi.project.Project

class ParameterUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  parameter: ParameterSegment
) : AbstractParameterUiModel(project, parent, parameter) {
  override fun createRenderer() = LeftTextHeaderAndRightContentRenderer(name, description)
}