package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ParagraphUiModel
import com.intelligentComments.ui.comments.model.content.ParameterUiModel
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.Renderer

interface SegmentRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getRendererFor(segment: ContentSegmentUiModel): SegmentRenderer {
      return when (segment) {
        is TextContentSegmentUiModel -> TextSegmentRenderer(segment)
        is ListContentSegmentUiModel -> ListSegmentRenderer(segment)
        is ImageContentSegmentUiModel -> ImageSegmentRenderer(segment)
        is TableContentSegmentUiModel -> TableSegmentRenderer(segment)
        is ParagraphUiModel -> ParagraphRendererImpl(segment)
        is ParameterUiModel -> ParameterRenderer(segment)
        is ReturnUiModel -> ReturnSegmentRenderer(segment)
        is RemarksUiModel -> RemarksSegmentRenderer(segment)
        is ExceptionUiModel -> ExceptionSegmentRenderer(segment)
        else -> throw IllegalArgumentException(segment.toString())
      }
    }
  }
}