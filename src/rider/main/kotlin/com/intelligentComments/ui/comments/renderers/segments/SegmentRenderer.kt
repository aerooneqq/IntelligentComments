package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.Renderer

interface SegmentRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(segment: ContentSegmentUiModel): SegmentRenderer {
            return when(segment) {
                is TextContentSegmentUiModel -> TextSegmentRenderer(segment)
                else -> throw IllegalArgumentException(segment.toString())
            }
        }
    }
}