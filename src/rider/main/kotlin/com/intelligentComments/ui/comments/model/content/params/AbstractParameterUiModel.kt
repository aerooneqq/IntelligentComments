package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.ArbitraryParamSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.getSecondLevelHeader
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

abstract class AbstractParameterUiModel(
  project: Project,
  parameter: ArbitraryParamSegment,
) : ContentSegmentUiModel(project, parameter) {
  val name = HighlightedTextUiWrapper(project, getSecondLevelHeader(project, parameter.name, parameter))

  val description = ContentSegmentsUiModel(project, parameter.content)

  override fun hashCode(): Int = HashUtil.hashCode(name.hashCode(), description.hashCode())
  override fun equals(other: Any?): Boolean = other is ParameterUiModel && other.hashCode() == hashCode()
}