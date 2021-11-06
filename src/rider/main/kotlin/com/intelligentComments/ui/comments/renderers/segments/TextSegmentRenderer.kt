package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max
import kotlin.test.assertNotNull

class TextSegmentRenderer(private val textSegment: TextContentSegmentUiModel) : SegmentRenderer {
    private val cachedLines = textSegment.text.split('\n')
    private val cachedLinesHighlighters = TextUtil.getLinesHighlighters(cachedLines, textSegment.highlighters)


    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        return TextUtil.renderLines(g, Rectangle(rect), editorImpl, cachedLines, textSegment.highlighters, 0)
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        var height = 0
        for (i in cachedLines.indices) {
            val lineHighlighters = cachedLinesHighlighters[i]
            assertNotNull(lineHighlighters, "cachedLinesHighlighters[i] != null")
            height += TextUtil.getLineHeightWithHighlighters(editorImpl, lineHighlighters)
        }

        return height
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        val fontMetrics = TextUtil.getFontMetrics(editorImpl, null)
        var maxWidth = 0

        for (line in cachedLines) {
            maxWidth = max(TextUtil.getTextWidth(fontMetrics, line), maxWidth)
        }

        return maxWidth
    }

    override fun accept(context: RectangleModelBuildContext) {
        TextUtil.createRectanglesForHighlighters(cachedLines, cachedLinesHighlighters, context)
    }
}