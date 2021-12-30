package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.image.DummyImageObserver
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RectanglesModelUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
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
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    rect.y += upperDelta
    val adjustedRect = renderImage(g, rect, editorImpl)
    return renderImageDescription(g, adjustedRect, editorImpl)
  }

  private fun renderImage(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
    val descriptionWidth = calculateDescriptionWidth(editorImpl)
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
    editorImpl: EditorImpl
  ): Rectangle {
    val text = model.description
    if (text != null) {
      val descriptionWidth = calculateDescriptionWidth(editorImpl)
      val imageWidth = model.imageHolder.width
      val initialX = rect.x

      val rectForText = if (imageWidth > descriptionWidth) {
        Rectangle(rect).apply {
          x = rect.x + (imageWidth - descriptionWidth) / 2
        }
      } else {
        rect
      }

      return TextUtil.renderLine(g, rectForText, editorImpl, text.text, text.highlighters, deltaAfterDescription)
        .apply {
          x = initialX
        }
    }

    return rect
  }

  private fun calculateDescriptionWidth(editorImpl: EditorImpl): Int {
    val description = model.description ?: return 0
    return TextUtil.getTextWidthWithHighlighters(editorImpl, description)
  }

  override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    val imageHeight = imageHolder.height + deltaBetweenImageAndDescription
    val descriptionHeight = getDescriptionHeight(editorImpl)
    return upperDelta + imageHeight + descriptionHeight
  }

  private fun getDescriptionHeight(editorImpl: EditorImpl): Int {
    val highlighters = model.description?.highlighters
    return if (highlighters != null) {
      TextUtil.getLineHeightWithHighlighters(editorImpl, highlighters) + deltaAfterDescription
    } else {
      0
    }
  }

  private fun getDescriptionWidth(editorImpl: EditorImpl): Int {
    val text = model.description
    return if (text != null) {
      TextUtil.getTextWidthWithHighlighters(editorImpl, text)
    } else {
      0
    }
  }

  override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    return max(imageHolder.width, getDescriptionWidth(editorImpl))
  }

  override fun accept(context: RectangleModelBuildContext) {
    context.rectanglesModel.addElement(model, Rectangle(context.rect).apply {
      y += upperDelta
      width = calculateExpectedWidthInPixels(context.editorImpl, context.additionalRenderInfo)
      height = calculateExpectedHeightInPixels(context.editorImpl, context.additionalRenderInfo)
    })
  }
}