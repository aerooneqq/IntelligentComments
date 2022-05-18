package com.intelligentcomments.ui.comments.model.content.params

import com.intelligentcomments.core.domain.core.ArbitraryParamSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.content.getSecondLevelHeader
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

abstract class AbstractParameterUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  parameter: ArbitraryParamSegment,
) : ContentSegmentUiModel(project, parent) {
  val name = HighlightedTextUiWrapper(project, this, getSecondLevelHeader(project, parameter.name.text, parameter))

  val description = ContentSegmentsUiModel(project, this, parameter.content)


  override fun dumpModel() = "${super.dumpModel()}::${name.dumpModel()}: \n{\n${description.dumpModel()}\n}"
  override fun calculateStateHash() = HashUtil.hashCode(name.calculateStateHash(), description.calculateStateHash())
}