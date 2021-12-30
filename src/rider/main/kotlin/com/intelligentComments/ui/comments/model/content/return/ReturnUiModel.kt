package com.intelligentComments.ui.comments.model.content.`return`

import com.intelligentComments.core.domain.core.ReturnSegment
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ReturnUiModel(project: Project, ret: ReturnSegment) : ContentSegmentUiModel(project, ret) {
  val content = ContentSegmentsUiModel(project, ret.content)

  private val highlightedHeader = getFirstLevelHeader(
    project,
    returnSectionName,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.ReturnBackgroundColor
  )

  val headerText = HighlightedTextUiWrapper(project, highlightedHeader)


  override fun hashCode(): Int = HashUtil.hashCode(content.hashCode())
  override fun equals(other: Any?): Boolean = other is ReturnUiModel && other.hashCode() == hashCode()
}

const val returnSectionName = "Returns"
