package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max
import kotlin.test.assertNotNull

open class TextRendererBase(private val textUiWrapper: HighlightedTextUiWrapper) : SegmentRenderer {
  private val cachedLines = textUiWrapper.text.split('\n')
  private val cachedLinesHighlighters
    get() = TextUtil.getLinesHighlighters(cachedLines, textUiWrapper.highlighters)


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    return TextUtil.renderLines(g, Rectangle(rect), editorImpl, cachedLines, textUiWrapper.highlighters, 0)
  }

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var height = 0
    for (i in cachedLines.indices) {
      val lineHighlighters = cachedLinesHighlighters[i]
      assertNotNull(lineHighlighters, "cachedLinesHighlighters[i] != null")
      height += TextUtil.getLineHeightWithHighlighters(editorImpl, lineHighlighters)
    }

    return height
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
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

abstract class TextRendererWithLeftFigure(
  textUiWrapper: HighlightedTextUiWrapper
) : TextRendererBase(textUiWrapper) {
  companion object {
    const val deltaBetweenLeftFigureAndText = 5
  }

  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val width = calculateLeftFigureWidth(editorImpl, additionalRenderInfo)
    renderLeftFigure(g, rect, editorImpl, rectanglesModel, additionalRenderInfo)

    val adjustedRect = Rectangle(rect).apply {
      x += width + deltaBetweenLeftFigureAndText
    }

    return super.render(g, adjustedRect, editorImpl, rectanglesModel, additionalRenderInfo).apply {
      x -= (width + deltaBetweenLeftFigureAndText)
    }
  }

  protected abstract fun renderLeftFigure(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  )

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val leftFigureWidth = calculateLeftFigureWidth(editorImpl, additionalRenderInfo)
    return leftFigureWidth + super.calculateExpectedWidthInPixels(editorImpl, additionalRenderInfo)
  }

  protected abstract fun calculateLeftFigureWidth(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int
}

class TextSegmentRenderer(
  textSegment: TextContentSegmentUiModel
) : TextRendererBase(textSegment.highlightedTextWrapper)