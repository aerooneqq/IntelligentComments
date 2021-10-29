package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.comments.model.invariants.TextInvariantUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

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
        g.drawString(invariant.text, rect.x + InvariantsRenderer.gapBetweenInvariants, textY)

        val newX = rect.x + ellipseWidth + InvariantsRenderer.gapBetweenInvariants
        val newWidth = rect.width - ellipseWidth - InvariantsRenderer.gapBetweenInvariants

        return Rectangle(newX, rect.y, newWidth, rect.height)
    }

    private fun calculateWidth(editorImpl: EditorImpl): Int {
        return TextUtil.getTextWidth(editorImpl, invariant.text) + 10
    }

    override fun calculateWidthWithInvariantInterval(editorImpl: EditorImpl): Int {
        return calculateExpectedWidthInPixels(editorImpl) + InvariantsRenderer.gapBetweenInvariants
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int = 20
    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int = calculateWidth(editorImpl)
}