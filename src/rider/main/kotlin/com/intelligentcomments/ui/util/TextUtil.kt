package com.intelligentcomments.ui.util

import com.intelligentcomments.core.domain.core.SquigglesKind
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentcomments.ui.colors.ColorName
import com.intelligentcomments.ui.colors.ColorsProvider
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.model.highlighters.HighlighterUiModel
import com.intelligentcomments.ui.core.AttributedCharsIterator
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import com.intellij.util.Range
import java.awt.*
import javax.swing.Icon
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertNotNull

class TextUtil {
  companion object {
    fun getFont(editor: Editor): Font {
      val size = RiderIntelligentCommentsSettingsProvider.getInstance().fontSize.value
      editor as EditorImpl
      return editor.getFontMetrics(Font.PLAIN).font.deriveFont(size)
    }

    fun getItalicFont(editor: Editor): Font {
      val size = RiderIntelligentCommentsSettingsProvider.getInstance().fontSize.value
      editor as EditorImpl
      return editor.getFontMetrics(Font.ITALIC).font.deriveFont(size)
    }

    fun getBoldFont(editor: Editor): Font {
      val size = RiderIntelligentCommentsSettingsProvider.getInstance().boldFontSize.value
      return getFont(editor).deriveFont(Font.BOLD).deriveFont(size)
    }

    const val backgroundArcDimension = 3

    const val deltaBetweenIconAndTextInHeader = 2
    private const val textHeightAdditionFactor = 0

    fun getFontMetrics(editor: Editor, highlighterUiModel: HighlighterUiModel?): FontMetrics {
      return when (highlighterUiModel?.style) {
        Font.PLAIN -> editor.contentComponent.getFontMetrics(getFont(editor))
        Font.BOLD -> editor.contentComponent.getFontMetrics(getBoldFont(editor))
        Font.ITALIC -> editor.contentComponent.getFontMetrics(getItalicFont(editor))
        else -> editor.contentComponent.getFontMetrics(getFont(editor))
      }
    }

    fun getTextWidth(editor: Editor, text: String): Int {
      val fontMetrics = getFontMetrics(editor, null)
      return fontMetrics.stringWidth(text)
    }

    fun getTextWidth(fontMetrics: FontMetrics, text: String) = fontMetrics.stringWidth(text)
    private fun getTextWidth(fontMetrics: FontMetrics, chars: CharArray, from: Int, to: Int): Int {
      return fontMetrics.charsWidth(chars, from, to - from + 1)
    }

    private fun getTextWidth(
      editor: Editor,
      chars: CharArray,
      from: Int,
      to: Int,
      highlighter: HighlighterUiModel?
    ): Int {
      val fontMetrics = getFontMetrics(editor, highlighter)
      return getTextWidth(fontMetrics, chars, from, to)
    }

    fun getTextWidth(editor: Editor, text: String, highlighter: HighlighterUiModel?): Int {
      return getFontMetrics(editor, highlighter).stringWidth(text)
    }

    fun getTextWidthWithHighlighters(
      editor: Editor,
      text: HighlightedTextUiWrapper
    ): Int {
      val line = text.text
      val highlighters = text.highlighters
      val charArray = line.toCharArray()

      val lineHighlighters = getLinesHighlighters(listOf(line), highlighters)[0]
      assertNotNull(lineHighlighters, "lineHighlighters != null")

      var width = 0
      executeActionOverRanges(charArray, lineHighlighters) { range, model ->
        width += getTextWidth(editor, charArray, range.from, range.to - 1, model)
      }

      return width
    }

    private fun getTextHeight(fontMetrics: FontMetrics) = fontMetrics.height + textHeightAdditionFactor

    fun getTextHeight(editor: Editor, highlighter: HighlighterUiModel?): Int {
      var height = getTextHeight(getFontMetrics(editor, highlighter))
      if (highlighter != null) {
        val backgroundStyle = highlighter.backgroundStyle
        if (backgroundStyle != null && backgroundStyle.roundedRect) {
          height += 3
        }
      }

      return height
    }

    fun renderText(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      text: String,
      delta: Int
    ): Rectangle {
      val metrics = getFontMetrics(editor, null)
      val textHeight = getTextHeight(editor, null)
      val adjustedRect = Rectangle(rect.x, rect.y + textHeight, rect.width, rect.height - textHeight)
      g.drawString(text, adjustedRect.x, adjustYCoordinateForTextDraw(adjustedRect.y, metrics))

      return Rectangle(adjustedRect.x, adjustedRect.y + delta, adjustedRect.width, adjustedRect.height - delta)
    }

    fun renderTextWithIcon(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      text: HighlightedTextUiWrapper,
      icon: Icon,
      gapBetweenTextAndIcon: Int,
      delta: Int
    ): Rectangle {
      val rectHeight = calculateTextHeightWithIcon(editor, icon, text)
      val contextComponent = editor.contentComponent
      icon.paintIcon(contextComponent, g, rect.x, rect.y)

      val adjustedRect = Rectangle(rect).apply {
        x += icon.iconWidth + gapBetweenTextAndIcon
      }

      renderLine(g, adjustedRect, editor, text.text, text.highlighters, delta)
      val finalDelta = rectHeight + delta
      return Rectangle(rect).apply {
        y += finalDelta
        height -= finalDelta
      }
    }

    fun calculateTextHeightWithIcon(
      editor: Editor,
      icon: Icon,
      text: HighlightedTextUiWrapper
    ): Int {
      val textHeight = getLineHeightWithHighlighters(editor, text.highlighters)
      return max(icon.iconHeight, textHeight)
    }

    fun calculateWidthOfTextWithIcon(
      editor: Editor,
      icon: Icon,
      gapBetweenTextAndIcon: Int,
      text: HighlightedTextUiWrapper
    ): Int {
      val textWidth = getTextWidthWithHighlighters(editor, text)
      return icon.iconWidth + gapBetweenTextAndIcon + textWidth
    }

    fun renderLines(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      lines: List<String>,
      delta: Int
    ): Rectangle {
      val metrics = getFontMetrics(editor, null)
      val textHeight = getTextHeight(editor, null)
      var textDelta = textHeight

      for (line in lines) {
        g.drawString(line, rect.x, adjustYCoordinateForTextDraw(rect.y + textDelta, metrics))
        textDelta += textHeight
      }

      val finalDelta = textDelta - textHeight + delta
      return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
    }

    fun renderLines(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      lines: List<String>,
      highlighters: Collection<HighlighterUiModel>,
      delta: Int
    ): Rectangle {
      val linesHighlighters = getLinesHighlighters(lines, highlighters)
      var textDelta = 0

      for (i in lines.indices) {
        val currentHighlighters = linesHighlighters[i]
        assertNotNull(currentHighlighters, "linesHighlighters[i] != null")

        val lineHeight = getLineHeightWithHighlighters(editor, currentHighlighters)
        textDelta += lineHeight

        val lineRect = Rectangle(rect).apply {
          y += textDelta
          height = lineHeight
        }

        renderLineWithHighlighters(g, lineRect, editor, lines[i], currentHighlighters)
      }

      val finalDelta = textDelta + delta
      return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
    }

    fun renderLine(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      text: HighlightedTextUiWrapper,
      delta: Int
    ): Rectangle {
      return renderLine(g, rect, editor, text.text, text.highlighters, delta)
    }

    fun renderLine(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      line: String,
      highlighters: Collection<HighlighterUiModel>,
      delta: Int
    ): Rectangle {
      val rangesWithHighlighters = getLinesHighlighters(listOf(line), highlighters)[0]
      assertNotNull(rangesWithHighlighters, "rangesWithHighlighters != null")
      val lineHeight = getLineHeightWithHighlighters(editor, rangesWithHighlighters)
      UpdatedRectCookie(rect, yDelta = lineHeight, heightDelta = lineHeight).use {
        renderLineWithHighlighters(g, rect, editor, line, rangesWithHighlighters)
      }

      val finalDelta = lineHeight + delta
      return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
    }

    fun getLineHeightWithHighlighters(
      editor: Editor,
      rangesWithHighlighters: Collection<RangeWithHighlighter>
    ): Int {
      return getLineHeightWithHighlighters(editor, rangesWithHighlighters.map { it.highlighter })
    }

    fun getLineHeightWithHighlighters(
      editor: Editor,
      highlightersModels: List<HighlighterUiModel?>
    ): Int {
      val heightWithoutHighlighter = getTextHeight(editor, null)
      var maxHeight = heightWithoutHighlighter
      for (model in highlightersModels) {
        maxHeight = max(maxHeight, getTextHeight(editor, model))
      }

      return maxHeight
    }

    private fun renderLineWithHighlighters(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      line: String,
      rangesWithHighlighters: List<RangeWithHighlighter>
    ) {
      var textLength = 0
      val chars = line.toCharArray()
      executeActionOverRanges(chars, rangesWithHighlighters) { range, model ->
        textLength += drawText(g, rect, editor, chars, range, textLength, model)
      }
    }

    private fun executeActionOverRanges(
      line: CharArray,
      lineHighlighters: List<RangeWithHighlighter>,
      actionWithTextRange: (Range<Int>, HighlighterUiModel?) -> Unit
    ) {
      var charIndex = 0
      var highlighterIndex = 0

      while (charIndex < line.size) {
        val highlighterModel = if (highlighterIndex >= lineHighlighters.size) {
          null
        } else {
          lineHighlighters[highlighterIndex]
        }

        if (highlighterModel == null) {
          actionWithTextRange(Range(charIndex, line.size), null)
          charIndex = line.size
        } else {
          assert(charIndex <= highlighterModel.range.from)

          if (charIndex < highlighterModel.range.from) {
            val range = Range(charIndex, highlighterModel.range.from)
            actionWithTextRange(range, null)
            charIndex = highlighterModel.range.from
          } else if (charIndex == highlighterModel.range.from) {
            val range = highlighterModel.range
            actionWithTextRange(range, highlighterModel.highlighter)
            highlighterIndex += 1
            charIndex = range.to
          }
        }
      }
    }

    private fun drawText(
      g: Graphics,
      rect: Rectangle,
      editor: Editor,
      line: CharArray,
      range: Range<Int>,
      currentTextLength: Int,
      highlighterModel: HighlighterUiModel?
    ): Int {
      val from = range.from
      val to = range.to
      val metrics = getFontMetrics(editor, highlighterModel)
      var textWidth = getTextWidth(editor, line, from, to - 1, highlighterModel)
      val textHeight = getTextHeight(editor, highlighterModel)

      var xDelta = 0

      val adjustedY = adjustYCoordinateForTextDraw(rect.y, metrics)
      if (highlighterModel == null) {
        g.drawChars(line, from, to - from, rect.x + currentTextLength, adjustedY)
      } else {
        val backgroundStyle = highlighterModel.backgroundStyle
        if (backgroundStyle != null) {
          textWidth += 2 * backgroundStyle.leftRightPadding
          xDelta = backgroundStyle.leftRightPadding
          UpdatedGraphicsCookie(g, color = backgroundStyle.backgroundColor).use {
            val y = rect.y - textHeight + metrics.descent / 2
            if (backgroundStyle.roundedRect) {
              g.fillRoundRect(rect.x, y, textWidth, textHeight, backgroundArcDimension, backgroundArcDimension)
            } else {
              g.fillRect(rect.x, y, textWidth, textHeight)
            }
          }
        }

        val squiggles = highlighterModel.squiggles
        val project = editor.project

        if (squiggles != null && project != null) {
          val color = project.service<ColorsProvider>().getColorFor(ColorName(squiggles.colorKey))
          val additionalYDeltaForSquiggles = 1
          drawSquiggles(g, textWidth, rect.x, adjustedY + additionalYDeltaForSquiggles, color, squiggles.kind)
        }

        val iterator = AttributedCharsIterator(editor, line, from, to, highlighterModel)
        g.drawString(iterator, rect.x + currentTextLength + xDelta, adjustedY)
      }

      return textWidth
    }

    private fun drawSquiggles(
      g: Graphics,
      width: Int,
      initialX: Int,
      initialY: Int,
      color: Color,
      kind: SquigglesKind
    ) {
      if (kind == SquigglesKind.Wave) {
        drawWaveSquiggles(g, width, initialX, initialY, color)
      } else if (kind == SquigglesKind.Dotted) {
        drawDottedSquiggles(g, width, initialX, initialY, color)
      }
    }

    private fun drawDottedSquiggles(
      g: Graphics,
      width: Int,
      initialX: Int,
      initialY: Int,
      color: Color,
      dotWidth: Int = 3,
      dotHeight: Int = 1,
      deltaBetweenDots: Int = 2
    ) {
      var currentX = initialX
      while (currentX < initialX + width) {
        UpdatedGraphicsCookie(g, color).use {
          g.fillRect(currentX, initialY, dotWidth, dotHeight)
          currentX += dotWidth + deltaBetweenDots
        }
      }
    }

    private fun drawWaveSquiggles(
      g: Graphics,
      width: Int,
      initialX: Int,
      initialY: Int,
      color: Color,
      squigglesHeight: Int = 2,
      wavePartWidth: Int = 2
    ) {
      val points = mutableListOf(Point(initialX, initialY + squigglesHeight))

      var currentX = initialX + wavePartWidth

      fun addNextPoint() {
        val lastPoint = points.last()
        if (lastPoint.y == initialY) {
          points.add(Point( lastPoint.x + wavePartWidth, lastPoint.y + squigglesHeight))
        } else {
          points.add(Point(lastPoint.x + wavePartWidth, initialY))
        }
      }
      while (currentX < initialX + width) {
        addNextPoint()
        currentX += wavePartWidth
      }


      if (points.last().y == initialY) {
        addNextPoint()
      }

      UpdatedGraphicsCookie(g, color = color).use {
        for (i in 0 until points.size - 1) {
          val firstPoint = points[i]
          val secondPoint = points[i + 1]
          g.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y)
        }
      }
    }

    private fun adjustYCoordinateForTextDraw(y: Int, metrics: FontMetrics) = y - metrics.descent

    data class RangeWithHighlighter(val range: Range<Int>, val highlighter: HighlighterUiModel?)

    fun getLinesHighlighters(
      lines: List<String>,
      highlightersModels: Collection<HighlighterUiModel>
    ): HashMap<Int, List<RangeWithHighlighter>> {
      val sortedHighlightersModels = highlightersModels.sortedBy { it.startOffset }
      val linesHighlighters = HashMap<Int, MutableList<RangeWithHighlighter>>()
      val lineRanges = mutableListOf(0)
      for (i in lines.indices) {
        val lastEndLineIndex = lineRanges.last()
        lineRanges.add(lines[i].length + lastEndLineIndex + 1)
        linesHighlighters[i] = mutableListOf()
      }

      for (highlighterModel in sortedHighlightersModels) {
        for (i in 1 until lineRanges.size) {
          val lineStartOffset = lineRanges[i - 1]
          val lineEndOffset = lineRanges[i]
          if (highlighterModel.startOffset < lineEndOffset && highlighterModel.endOffset >= lineStartOffset) {
            val highlighterLineRangeStart = max(highlighterModel.startOffset, lineStartOffset)
            val highlighterLineRangeEnd = min(highlighterModel.endOffset, lineEndOffset - 1)
            val highlighterRangeForLine = Range<Int>(highlighterLineRangeStart - lineStartOffset, highlighterLineRangeEnd - lineStartOffset)
            val rangeWithHighlighter = RangeWithHighlighter(highlighterRangeForLine, highlighterModel)
            linesHighlighters[i - 1]!!.add(rangeWithHighlighter)
          }
        }
      }

      val resultingLineHighlighters = HashMap<Int, List<RangeWithHighlighter>>().apply {
        for ((index, highlightersWithRange) in linesHighlighters) {
          this[index] = highlightersWithRange
        }
      }

      assertNoOverlappingHighlighters(resultingLineHighlighters)
      return resultingLineHighlighters
    }

    private fun assertNoOverlappingHighlighters(lineHighlightersWithRanges: HashMap<Int, List<RangeWithHighlighter>>) {
      for ((_, highlightersWithRange) in lineHighlightersWithRanges) {
        for (i in 1 until highlightersWithRange.size) {
          assert(highlightersWithRange[i - 1].range.to <= highlightersWithRange[i].range.from)
        }
      }
    }

    fun createRectanglesForHighlightedText(
      text: HighlightedTextUiWrapper,
      context: RectangleModelBuildContext
    ) = createRectanglesForHighlighters(text.text, text.highlighters, context)

    fun createRectanglesForHighlighters(
      line: String,
      highlighters: Collection<HighlighterUiModel>,
      context: RectangleModelBuildContext
    ) {
      val lines = mutableListOf(line)
      val linesHighlighters = getLinesHighlighters(lines, highlighters)
      createRectanglesForHighlighters(lines, linesHighlighters, context)
    }

    fun createRectanglesForHighlighters(
      lines: List<String>,
      linesHighlighters: HashMap<Int, List<RangeWithHighlighter>>,
      context: RectangleModelBuildContext
    ) {
      val editor = context.editor
      val rect = context.rect
      var yDelta = 0
      val project = editor.project

      assertNotNull(project, "editor.project != null")
      for (i in lines.indices) {
        val chars = lines[i].toCharArray()
        val lineHighlighters = linesHighlighters[i]
        assertNotNull(lineHighlighters, "linesHighlighters[i] != null")

        var textLength = 0
        val lineHeight = getLineHeightWithHighlighters(editor, lineHighlighters)
        executeActionOverRanges(chars, lineHighlighters) { range, model ->
          val backgroundXDelta = if (model?.backgroundStyle != null) backgroundArcDimension else 0
          val textWidth = getTextWidth(editor, chars, range.from, range.to - 1, model) + backgroundXDelta

          if (model != null) {
            val highlighterRect = Rectangle(rect.x + textLength, rect.y + yDelta, textWidth, lineHeight)
            context.rectanglesModel.addElement(model, highlighterRect)
          }

          textLength += textWidth
        }

        yDelta += lineHeight
      }
    }
  }
}