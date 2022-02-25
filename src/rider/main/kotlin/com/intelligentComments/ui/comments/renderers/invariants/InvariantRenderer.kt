package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.use
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.Icon

interface InvariantRenderer : SegmentRenderer {
  companion object {
    const val invariantHeight = 20
    const val deltaBetweenTextAndIcon = 0
    const val extraInvariantWidth = 10
  }

  fun calculateWidthWithInvariantInterval(editor: Editor, additionalRendererInfo: RenderAdditionalInfo): Int
}

class InvariantRendererUtil {
  companion object {
    fun render(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      borderColor: Color,
      backgroundColor: Color,
      width: Int,
      text: String,
      icon: Icon? = null
    ): Rectangle {
      UpdatedGraphicsCookie(g, color = backgroundColor).use {
        g.fillRoundRect(rect.x, rect.y, width, rect.height, 3, 3)
      }

      UpdatedGraphicsCookie(g, color = borderColor).use {
        g.drawRoundRect(rect.x, rect.y, width, rect.height, 3, 3)
      }

      val newX = rect.x + InvariantsRenderer.gapBetweenInvariants

      if (icon != null) {
        val yDelta = (rect.height - icon.iconHeight) / 2
        icon.paintIcon(editor.contentComponent, g, newX, rect.y + yDelta)
      }

      val xDelta = if (icon == null) 0 else icon.iconWidth + InvariantRenderer.deltaBetweenTextAndIcon

      val textY = rect.y + 3 * rect.height / 4 - 1
      g.drawString(text, newX + xDelta, textY)

      val newWidth = rect.width - width - InvariantsRenderer.gapBetweenInvariants

      return Rectangle(newX + width, rect.y, newWidth, rect.height)
    }
  }
}