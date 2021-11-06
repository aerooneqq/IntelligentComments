package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.*
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.Renderer

interface SegmentRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(segment: ContentSegmentUiModel): SegmentRenderer {
            return when(segment) {
                is TextContentSegmentUiModel -> TextSegmentRenderer(segment)
                is ListContentSegmentUiModel -> ListSegmentRenderer(segment)
                is ImageContentSegmentUiModel -> ImageSegmentRenderer(segment)
                is TableContentSegmentUiModel -> TableSegmentRenderer(segment)
                is ParagraphUiModel -> ParagraphRendererImpl(segment)
                is ParameterUiModel -> ParameterRenderer(segment)
                else -> throw IllegalArgumentException(segment.toString())
            }
        }
    }
}