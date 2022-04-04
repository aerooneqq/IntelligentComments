package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.image.DummyImageObserver
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RectanglesModelUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class ImageSegmentRenderer(private val model: ImageContentSegmentUiModel) : SegmentRenderer {
  companion object {
    const val deltaBetweenImageAndDescription = 2
    const val deltaAfterDescription = 4
    const val upperDelta = 5
  }

  private val imageHolder = model.imageHolder

  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    rect.y += upperDelta
    val adjustedRect = renderImage(g, rect, editor)
    return renderImageDescription(g, adjustedRect, editor)
  }

  private fun renderImage(g: Graphics, rect: Rectangle, editor: Editor): Rectangle {
    val descriptionWidth = calculateDescriptionWidth(editor)
    val imageWidth = model.imageHolder.width
    val initialX = rect.x

    val adjustedX = if (descriptionWidth > imageWidth) {
      rect.x + (descriptionWidth - imageWidth) / 2
    } else {
      rect.x
    }

    g.drawImage(
      imageHolder.image,
      adjustedX,
      rect.y,
      imageHolder.width,
      imageHolder.height,
      DummyImageObserver.instance
    )

    val adjustedRect = Rectangle(rect)
    RectanglesModelUtil.addHeightDelta(adjustedRect, imageHolder.height)
    RectanglesModelUtil.addHeightDelta(adjustedRect, deltaBetweenImageAndDescription)
    return adjustedRect.apply {
      x = initialX
    }
  }

  private fun renderImageDescription(
    g: Graphics,
    rect: Rectangle,
    editor: Editor
  ): Rectangle {
    val text = model.description
    if (text != null) {
      val descriptionWidth = calculateDescriptionWidth(editor)
      val imageWidth = model.imageHolder.width
      val initialX = rect.x

      val rectForText = if (imageWidth > descriptionWidth) {
        Rectangle(rect).apply {
          x = rect.x + (imageWidth - descriptionWidth) / 2
        }
      } else {
        rect
      }

      return TextUtil.renderLine(g, rectForText, editor, text.text, text.highlighters, deltaAfterDescription)
        .apply {
          x = initialX
        }
    }

    return rect
  }

  private fun calculateDescriptionWidth(editor: Editor): Int {
    val description = model.description ?: return 0
    return TextUtil.getTextWidthWithHighlighters(editor, description)
  }

  override fun calculateExpectedHeightInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    val imageHeight = imageHolder.height + deltaBetweenImageAndDescription
    val descriptionHeight = getDescriptionHeight(editor)
    return upperDelta + imageHeight + descriptionHeight
  }

  private fun getDescriptionHeight(editor: Editor): Int {
    val highlighters = model.description?.highlighters
    return if (highlighters != null) {
      TextUtil.getLineHeightWithHighlighters(editor, highlighters) + deltaAfterDescription
    } else {
      0
    }
  }

  private fun getDescriptionWidth(editor: Editor): Int {
    val text = model.description
    return if (text != null) {
      TextUtil.getTextWidthWithHighlighters(editor, text)
    } else {
      0
    }
  }

  override fun calculateExpectedWidthInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    return max(imageHolder.width, getDescriptionWidth(editor))
  }

  override fun accept(context: RectangleModelBuildContext) {
    context.rectanglesModel.addElement(model, Rectangle(context.rect).apply {
      y += upperDelta
      width = calculateExpectedWidthInPixels(context.editor, context.additionalRenderInfo)
      height = calculateExpectedHeightInPixels(context.editor, context.additionalRenderInfo)
    })
  }
}