package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.TypeParamSegment
import com.intellij.openapi.project.Project

class TypeParamUiModel(
  project: Project,
  typeParam: TypeParamSegment
) : AbstractParameterUiModel(project, typeParam)