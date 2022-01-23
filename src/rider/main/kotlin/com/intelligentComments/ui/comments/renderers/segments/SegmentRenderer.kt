package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.code.CodeSegmentUiModel
import com.intelligentComments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.GroupedExceptionUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedTypeParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.ParameterUiModel
import com.intelligentComments.ui.comments.model.content.params.TypeParamUiModel
import com.intelligentComments.ui.comments.model.content.remarks.GroupedRemarksUiModel
import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.model.content.`return`.GroupedReturnUiModel
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.summary.GroupedSummaryUiModel
import com.intelligentComments.ui.comments.model.content.summary.SummaryUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.value.ValueUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.Renderer

interface SegmentRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getRendererFor(segment: ContentSegmentUiModel): SegmentRenderer {
      //ToDo: sth is definitely wrong here
      return when (segment) {
        is TextContentSegmentUiModel -> TextSegmentRenderer(segment)
        is ListContentSegmentUiModel -> ListSegmentRenderer(segment)
        is ImageContentSegmentUiModel -> ImageSegmentRenderer(segment)
        is TableContentSegmentUiModel -> TableSegmentRenderer(segment)
        is ParagraphUiModel -> ParagraphRendererImpl(segment)
        is ParameterUiModel -> ParameterRenderer(segment)
        is TypeParamUiModel -> ParameterRenderer(segment)
        is ReturnUiModel -> ReturnSegmentRenderer(segment)
        is RemarksUiModel -> RemarksSegmentRenderer(segment)
        is ExceptionUiModel -> ExceptionSegmentRenderer(segment)
        is SummaryUiModel -> SummarySegmentRenderer(segment)
        is SeeAlsoUiModel -> SeeAlsoSegmentRenderer.getFor(segment)
        is GroupedSeeAlsoUiModel -> GroupedSeeAlsoRenderer(segment)
        is GroupedReturnUiModel -> GroupedReturnsRenderer(segment)
        is GroupedParamsUiModel -> GroupedParamsRenderer(segment)
        is GroupedTypeParamsUiModel -> GroupedTypeParamsRenderer(segment)
        is GroupedExceptionUiModel -> GroupedExceptionsRenderer(segment)
        is GroupedSummaryUiModel -> GroupedSummariesRenderer(segment)
        is GroupedRemarksUiModel -> GroupedRemarksRenderer(segment)
        is ExampleSegmentUiModel -> ExampleSegmentRenderer(segment)
        is CodeSegmentUiModel -> CodeSegmentRenderer(segment)
        is ValueUiModel -> ValueSegmentRenderer(segment)
        else -> throw IllegalArgumentException(segment.toString())
      }
    }
  }
}