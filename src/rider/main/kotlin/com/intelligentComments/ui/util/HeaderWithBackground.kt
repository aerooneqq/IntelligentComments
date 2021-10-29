package com.intelligentComments.ui.util

import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle

class HeaderWithBackground {
    companion object {
        private const val margin = 5

        fun drawTextWithBackground(g: Graphics,
                                   rect: Rectangle,
                                   editorImpl: EditorImpl,
                                   text: String,
                                   color: Color): Rectangle {
            val height = calculateHeaderHeight(editorImpl)
            val width = calculateHeaderWidth(editorImpl, text)
            val adjustedRect = drawBackgroundRoundedRect(g, rect, width, height, color)

            return drawName(g, adjustedRect, editorImpl, text, height)
        }

        fun calculateHeaderHeight(editorImpl: EditorImpl): Int {
            return CommentsUtil.getTextHeight(editorImpl, null)
        }

        fun calculateHeaderWidth(editorImpl: EditorImpl, text: String): Int {
            val textWidth = CommentsUtil.getTextWidth(editorImpl, text)
            return textWidth + 2 * margin
        }

        private fun drawBackgroundRoundedRect(g: Graphics,
                                              rect: Rectangle,
                                              headerWidth: Int,
                                              headerHeight: Int,
                                              color: Color): Rectangle {
            UpdatedGraphicsCookie(g, color = color).use {
                g.fillRoundRect(rect.x, rect.y, headerWidth, headerHeight, 3, 3)
            }

            return rect
        }

        private fun drawName(g: Graphics,
                             rect: Rectangle,
                             editorImpl: EditorImpl,
                             text: String,
                             height: Int): Rectangle {
            val shift = calculateHeightShiftForName(editorImpl)
            val rectForText = Rectangle(rect.x + margin, rect.y - shift, rect.width, rect.height)
            CommentsUtil.renderText(g, rectForText, editorImpl, text, 0)

            return Rectangle(rect.x, rect.y + height, rect.width, rect.height - height)
        }

        private fun calculateHeightShiftForName(editorImpl: EditorImpl) = CommentsUtil.getTextHeight(editorImpl, null) / 4
    }
}