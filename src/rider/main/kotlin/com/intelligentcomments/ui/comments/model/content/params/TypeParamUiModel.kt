package com.intelligentcomments.ui.comments.model.content.params

import com.intelligentcomments.core.domain.core.TypeParamSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

class TypeParamUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  typeParam: TypeParamSegment
) : AbstractParameterUiModel(project, parent, typeParam) {
  override fun createRenderer(): Renderer {
    return LeftTextHeaderAndRightContentRenderer(name, description)
  }
}