package com.intelligentComments.ui.comments.model.content.`return`

import com.intelligentComments.core.domain.core.ReturnSegment
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.ReturnSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ReturnUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  ret: ReturnSegment
) : ContentSegmentUiModel(project, parent, ret) {
  val content = ContentSegmentsUiModel(project, this, ret.content)

  private val highlightedHeader = getFirstLevelHeader(
    project,
    returnSectionName,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.ReturnBackgroundColor,
    ret
  )

  val headerText = HighlightedTextUiWrapper(project, this, highlightedHeader)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return ReturnSegmentRenderer(this)
  }
}

const val returnSectionName = "Returns"
