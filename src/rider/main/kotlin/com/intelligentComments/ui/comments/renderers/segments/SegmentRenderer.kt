package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.CommentsUtil
import com.intelligentComments.ui.comments.model.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface SegmentRenderer : Renderer {
    companion object {
        fun getRendererFor(segment: ContentSegmentUiModel): SegmentRenderer {
            return when(segment) {
                is TextContentSegmentUiModel -> TextSegmentRenderer(segment)
                else -> throw IllegalArgumentException(segment.toString())
            }
        }
    }
}

class TextSegmentRenderer(private val textSegment: TextContentSegmentUiModel) : SegmentRenderer {
    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        val lines = getLines() ?: return rect
        return CommentsUtil.renderLines(g, Rectangle(rect).apply {
            y -= 3
        }, editorImpl, lines, 0).apply {
            y += 3
        }
    }

    private fun getLines(): List<String>? {
        return textSegment.text?.split('\n')
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        val lines = getLines() ?: return 0
        return lines.size * (CommentsUtil.getTextHeight(editorImpl) + CommentsUtil.getLineInterval(editorImpl))
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        val fontMetrics = CommentsUtil.getFontMetrics(editorImpl)
        var maxWidth = 0

        for (line in (getLines() ?: return 0)) {
            maxWidth = max(CommentsUtil.getTextWidth(fontMetrics, line), maxWidth)
        }

        return maxWidth
    }
}