package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.*
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
                else -> throw IllegalArgumentException(segment.toString())
            }
        }
    }
}