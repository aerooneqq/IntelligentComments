package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intelligentcomments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max
import kotlin.test.assertNotNull

open class TextRendererBase(private val textUiWrapper: HighlightedTextUiWrapper) : SegmentRenderer {
  private val cachedLines = textUiWrapper.text.split('\n')
  private val cachedLinesHighlighters = TextUtil.getLinesHighlighters(cachedLines, textUiWrapper.highlighters)


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    return TextUtil.renderLines(g, Rectangle(rect), editor, cachedLines, textUiWrapper.highlighters, 0)
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var height = 0
    for (i in cachedLines.indices) {
      val lineHighlighters = cachedLinesHighlighters[i]
      assertNotNull(lineHighlighters, "cachedLinesHighlighters[i] != null")
      height += TextUtil.getLineHeightWithHighlighters(editor, lineHighlighters)
    }

    return height
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val fontMetrics = TextUtil.getFontMetrics(editor, null)
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
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val width = calculateLeftFigureWidth(editor, additionalRenderInfo)
    renderLeftFigure(g, rect, editor, rectanglesModel, additionalRenderInfo)

    val adjustedRect = Rectangle(rect).apply {
      x += width + deltaBetweenLeftFigureAndText
    }

    return super.render(g, adjustedRect, editor, rectanglesModel, additionalRenderInfo).apply {
      x -= (width + deltaBetweenLeftFigureAndText)
    }
  }

  protected abstract fun renderLeftFigure(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  )

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val leftFigureWidth = calculateLeftFigureWidth(editor, additionalRenderInfo)
    return leftFigureWidth + super.calculateExpectedWidthInPixels(editor, additionalRenderInfo)
  }

  protected abstract fun calculateLeftFigureWidth(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int
}

class TextSegmentRenderer(
  textSegment: TextContentSegmentUiModel
) : TextRendererBase(textSegment.highlightedTextWrapper)