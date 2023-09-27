package com.intelligentcomments.ui.comments.model.content.`return`

import com.intelligentcomments.core.domain.core.ReturnSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ReturnUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  ret: ReturnSegment
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, ret.content)

  private val highlightedHeader = getFirstLevelHeader(
    project,
    returnSectionName,
    ret
  )

  val headerText = HighlightedTextUiWrapper(project, this, highlightedHeader)


  override fun dumpModel() = "${super.dumpModel()}::${headerText.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash() = HashUtil.hashCode(content.calculateStateHash())
  override fun createRenderer() = LeftTextHeaderAndRightContentRenderer(headerText, content)
}

const val returnSectionName = "Returns"
