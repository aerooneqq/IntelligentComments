package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.CommentsUtil
import com.intelligentComments.ui.CommentsUtil.Companion.deltaBetweenHeaderAndContent
import com.intelligentComments.ui.comments.model.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer.Companion.deltaBetweenSegments
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface SegmentsRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        const val deltaBetweenSegments = 3

        fun getRendererFor(segmentsSection: SectionWithHeaderUiModel<ContentSegmentUiModel>): SegmentsRenderer {
            return SegmentsRendererImpl(segmentsSection)
        }
    }
}

class SegmentsRendererImpl(private val segmentsSection: SectionWithHeaderUiModel<ContentSegmentUiModel>)
    : VerticalSectionWithHeaderRenderer<ContentSegmentUiModel>(segmentsSection), SegmentsRenderer {

    override fun renderContent(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               rectanglesModel: RectanglesModel): Rectangle {
        var adjustedRect = rect
        for (segment in segmentsSection.content) {
            val renderer = SegmentRenderer.getRendererFor(segment)
            adjustedRect = renderer.render(g, adjustedRect, editorImpl, rectanglesModel)
            CommentsUtil.addHeightDelta(adjustedRect, deltaBetweenSegments)
        }

        CommentsUtil.addHeightDelta(adjustedRect, -deltaBetweenSegments)
        return adjustedRect
    }

    override fun calculateContentHeight(editorImpl: EditorImpl): Int {
        var height = super.calculateExpectedHeightInPixels(editorImpl)
        for (segment in segmentsSection.content) {
            val renderer = SegmentRenderer.getRendererFor(segment)
            height += renderer.calculateExpectedHeightInPixels(editorImpl)
        }

        return height
    }

    override fun calculateContentWidth(editorImpl: EditorImpl): Int {
        var width = super.calculateExpectedWidthInPixels(editorImpl)
        for (segment in segmentsSection.content) {
            val renderer = SegmentRenderer.getRendererFor(segment)
            width = max(width, renderer.calculateExpectedWidthInPixels(editorImpl))
        }

        return width
    }

    override fun acceptContent(context: RectangleModelBuildContext) {
        CommentsUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, deltaBetweenHeaderAndContent)

        for (segment in segmentsSection.content) {
            val renderer = SegmentRenderer.getRendererFor(segment)
            renderer.accept(context)
            CommentsUtil.updateHeightAndAddModel(renderer, context, segment)
            CommentsUtil.addHeightDeltaTo(context, deltaBetweenSegments)
        }

        CommentsUtil.addHeightDeltaTo(context, -deltaBetweenSegments)
    }
}