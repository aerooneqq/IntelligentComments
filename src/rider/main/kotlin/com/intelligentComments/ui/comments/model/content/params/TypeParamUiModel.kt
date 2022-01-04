package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.TypeParamSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

class TypeParamUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  typeParam: TypeParamSegment
) : AbstractParameterUiModel(project, parent, typeParam)