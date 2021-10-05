package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.CommentsUtil
import com.intelligentComments.ui.UpdatedGraphicsCookie
import com.intelligentComments.ui.comments.model.InvariantUiModel
import com.intelligentComments.ui.comments.model.TextInvariantUiModel
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer.Companion.gapBetweenInvariants
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

interface InvariantRenderer : Renderer {
    companion object {
        fun getRendererFor(invariant: InvariantUiModel): InvariantRenderer {
            return when(invariant) {
                is TextInvariantUiModel -> TextDefaultInvariantRenderer(invariant)
                else -> throw IllegalArgumentException(invariant.toString())
            }
        }
    }

    fun calculateWidthWithInvariantInterval(editorImpl: EditorImpl): Int
}

class TextDefaultInvariantRenderer(private val invariant: TextInvariantUiModel) : InvariantRenderer {
    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        val ellipseWidth = calculateWidth(editorImpl)

        UpdatedGraphicsCookie(g, invariant.backgroundColor).use {
            g.fillRoundRect(rect.x, rect.y, ellipseWidth, rect.height, 3, 3)
        }

        val textY = rect.y + 3 * rect.height / 4 - 1
        g.drawString(invariant.text, rect.x + gapBetweenInvariants, textY)

        val newX = rect.x + ellipseWidth + gapBetweenInvariants
        val newWidth = rect.width - ellipseWidth - gapBetweenInvariants

        return Rectangle(newX, rect.y, newWidth, rect.height)
    }

    private fun calculateWidth(editorImpl: EditorImpl): Int {
        return CommentsUtil.getTextWidth(CommentsUtil.getFontMetrics(editorImpl), invariant.text) + 10
    }

    override fun calculateWidthWithInvariantInterval(editorImpl: EditorImpl): Int {
        return calculateExpectedWidthInPixels(editorImpl) + gapBetweenInvariants
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int = 20
    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int = calculateWidth(editorImpl)
}