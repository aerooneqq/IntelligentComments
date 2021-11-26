package com.intelligentComments.ui.util

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RectanglesModelUtil.Companion.deltaBetweenHeaderAndContent
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class ContentSegmentsUtil {
    companion object {
        const val deltaBetweenSegments = 3

        fun renderSegments(contentSegments: Collection<ContentSegmentUiModel>,
                           g: Graphics,
                           rect: Rectangle,
                           editorImpl: EditorImpl,
                           rectanglesModel: RectanglesModel): Rectangle {
            var adjustedRect = Rectangle(rect)
            executeWithRenderers(contentSegments) { renderer, _ ->
                adjustedRect = renderer.render(g, adjustedRect, editorImpl, rectanglesModel)
                RectanglesModelUtil.addHeightDelta(adjustedRect, deltaBetweenSegments)
            }

            RectanglesModelUtil.addHeightDelta(adjustedRect, -deltaBetweenSegments)
            return adjustedRect
        }

        private fun executeWithRenderers(contentSegments: Collection<ContentSegmentUiModel>,
                                         action: (SegmentRenderer, ContentSegmentUiModel) -> Unit) {
            for (segment in contentSegments) {
                val renderer = SegmentRenderer.getRendererFor(segment)
                action(renderer, segment)
            }
        }

        fun calculateContentHeight(contentSegments: Collection<ContentSegmentUiModel>,
                                   editorImpl: EditorImpl): Int {
            var height = 0
            executeWithRenderers(contentSegments) { renderer, _ ->
                height += renderer.calculateExpectedHeightInPixels(editorImpl) + deltaBetweenSegments
            }

            return height - deltaBetweenSegments
        }

        fun calculateContentWidth(contentSegments: Collection<ContentSegmentUiModel>,
                                  editorImpl: EditorImpl): Int {
            var width = 0
            executeWithRenderers(contentSegments) { renderer, _ ->
                width = max(width, renderer.calculateExpectedWidthInPixels(editorImpl))
            }

            return width
        }

        fun accept(context: RectangleModelBuildContext, segments: Collection<ContentSegmentUiModel>) {
            executeWithRenderers(segments) { renderer, segment ->
                renderer.accept(context)
                RectanglesModelUtil.updateHeightAndWidthAndAddModel(renderer, context, segment)
                RectanglesModelUtil.addHeightDeltaTo(context, deltaBetweenSegments)
            }

            if (segments.size > 0) {
                RectanglesModelUtil.addHeightDeltaTo(context, -deltaBetweenSegments)
            }
        }
    }
}