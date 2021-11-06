package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ParameterUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

class ParameterRenderer(private val model: ParameterUiModel) : SegmentRenderer {
    companion object {
        private const val deltaBetweenNameAndDescription = 10
    }


    override fun render(
        g: Graphics,
        rect: Rectangle,
        editorImpl: EditorImpl,
        rectanglesModel: RectanglesModel
    ): Rectangle {
        renderName(g, rect, editorImpl)
        val nameWidth = calculateNameWidth(editorImpl)
        val adjustedRect = Rectangle(rect).apply {
            x += nameWidth + deltaBetweenNameAndDescription
            y = rect.y + calculateNameHeight(editorImpl) / 8
        }

        return ContentSegmentsUtil.renderSegments(model.description.content, g, adjustedRect, editorImpl, rectanglesModel)
    }

    private fun renderName(g: Graphics,
                           rect: Rectangle,
                           editorImpl: EditorImpl): Rectangle {
        return TextUtil.renderLine(g, rect, editorImpl, model.name.text, model.name.highlighters, 0)
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        val nameHeight = calculateNameHeight(editorImpl)
        val contentHeight = ContentSegmentsUtil.calculateContentHeight(model.description.content, editorImpl)
        return max(nameHeight, contentHeight) + nameHeight / 8
    }

    private fun calculateNameHeight(editorImpl: EditorImpl): Int {
        return TextUtil.getLineHeightWithHighlighters(editorImpl, model.name.highlighters) + 2
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        var width = calculateNameWidth(editorImpl)
        width += deltaBetweenNameAndDescription
        width += ContentSegmentsUtil.calculateContentWidth(model.description.content, editorImpl)
        return width
    }

    private fun calculateNameWidth(editorImpl: EditorImpl): Int {
        return TextUtil.getTextWidthWithHighlighters(editorImpl, model.name)
    }

    override fun accept(context: RectangleModelBuildContext) {

    }
}