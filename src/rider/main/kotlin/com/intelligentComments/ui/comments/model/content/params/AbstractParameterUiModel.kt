package com.intelligentComments.ui.comments.model.content.params

import com.intelligentComments.core.domain.core.ArbitraryParamSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.getSecondLevelHeader
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

abstract class AbstractParameterUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  parameter: ArbitraryParamSegment,
) : ContentSegmentUiModel(project, parent, parameter) {
  val name = HighlightedTextUiWrapper(project, this, getSecondLevelHeader(project, parameter.name, parameter))

  val description = ContentSegmentsUiModel(project, this, parameter.content)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(name.calculateStateHash(), description.calculateStateHash())
  }
}