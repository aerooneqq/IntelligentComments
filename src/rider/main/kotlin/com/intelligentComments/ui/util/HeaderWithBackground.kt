package com.intelligentComments.ui.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.use
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle

class HeaderWithBackground {
  companion object {
    private const val margin = 5

    fun drawTextWithBackground(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      text: String,
      color: Color
    ): Rectangle {
      val height = calculateHeaderHeight(editor)
      val width = calculateHeaderWidth(editor, text)
      val adjustedRect = drawBackgroundRoundedRect(g, rect, width, height, color)

      return drawName(g, adjustedRect, editor, text, height)
    }

    fun calculateHeaderHeight(editor: Editor): Int {
      return TextUtil.getTextHeight(editor, null)
    }

    fun calculateHeaderWidth(editor: Editor, text: String): Int {
      val textWidth = TextUtil.getTextWidth(editor, text)
      return textWidth + 2 * margin
    }

    private fun drawBackgroundRoundedRect(
      g: Graphics,
      rect: Rectangle,
      headerWidth: Int,
      headerHeight: Int,
      color: Color
    ): Rectangle {
      UpdatedGraphicsCookie(g, color = color).use {
        g.fillRoundRect(rect.x, rect.y, headerWidth, headerHeight, 3, 3)
      }

      return rect
    }

    private fun drawName(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      text: String,
      height: Int
    ): Rectangle {
      val shift = calculateHeightShiftForName(editor)
      val rectForText = Rectangle(rect.x + margin, rect.y - shift, rect.width, rect.height)
      TextUtil.renderText(g, rectForText, editor, text, 0)

      return Rectangle(rect.x, rect.y + height, rect.width, rect.height - height)
    }

    private fun calculateHeightShiftForName(editor: Editor) = TextUtil.getTextHeight(editor, null) / 4
  }
}