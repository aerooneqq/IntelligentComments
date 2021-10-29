package com.intelligentComments.ui

import com.intelligentComments.ui.comments.model.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.model.HighlighterUiModel
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.CommentAuthorsRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer
import com.intelligentComments.ui.comments.renderers.references.DependencyReferenceRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.comments.renderers.todos.ToDosRenderer
import com.intelligentComments.ui.core.AttributedCharsIterator
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import com.intellij.util.Range
import com.intellij.util.ui.UIUtil
import java.awt.*
import javax.swing.Icon
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertNotNull

class UpdatedGraphicsCookie(private val graphics: Graphics,
                            color: Color = graphics.color,
                            font: Font = graphics.font) : Disposable {
    private val previousColor = graphics.color
    private val previousFont = graphics.font

    init {
        graphics.color = color
        graphics.font = font
    }

    override fun dispose() {
        graphics.color = previousColor
        graphics.font = previousFont
    }
}

class UpdatedRectCookie(private val rect: Rectangle,
                        private val xDelta: Int = 0,
                        private val yDelta: Int = 0,
                        private val widthDelta: Int = 0,
                        private val heightDelta: Int = 0) : Disposable {
    private val initialRect = Rectangle(rect)

    init {
        rect.apply {
            x += xDelta
            y += yDelta
            width += widthDelta
            height += heightDelta
        }
    }

    override fun dispose() {
        rect.apply {
            x = initialRect.x
            y = initialRect.y
            width = initialRect.width
            height = initialRect.height
        }
    }
}

class WidthAndHeight {
    var width = 0
    var height = 0
}

class CommentsUtil {
    companion object {
        val font: Font = UIUtil.getLabelFont().deriveFont(12f)
        val boldFont: Font = font.deriveFont(Font.BOLD).deriveFont(14f)

        private const val minCommentHeightPx = 0
        const val heightDeltaBetweenSections = 10
        const val deltaBetweenHeaderAndContent = 5
        const val deltaBetweenIconAndTextInHeader = 2
        const val textHeightAdditionFactor = 2

        fun getFontMetrics(editorImpl: EditorImpl, highlighterUiModel: HighlighterUiModel?): FontMetrics {
            return when(highlighterUiModel?.style) {
                Font.PLAIN -> editorImpl.contentComponent.getFontMetrics(font)
                Font.BOLD -> editorImpl.contentComponent.getFontMetrics(boldFont)
                else -> editorImpl.contentComponent.getFontMetrics(font)
            }
        }

        fun getTextWidth(editorImpl: EditorImpl, text: String): Int {
            val fontMetrics = getFontMetrics(editorImpl, null)
            return fontMetrics.stringWidth(text)
        }

        fun getTextWidth(fontMetrics: FontMetrics, text: String) = fontMetrics.stringWidth(text)
        private fun getTextWidth(fontMetrics: FontMetrics, chars: CharArray, from: Int, to: Int): Int {
            return fontMetrics.charsWidth(chars, from, to - from + 1)
        }

        fun getTextWidth(editorImpl: EditorImpl, chars: CharArray, from: Int, to: Int, highlighter: HighlighterUiModel?): Int {
            val fontMetrics = getFontMetrics(editorImpl, highlighter)
            return getTextWidth(fontMetrics, chars, from, to)
        }

        fun getTextWidth(editorImpl: EditorImpl, text: String, highlighter: HighlighterUiModel?): Int {
            return getFontMetrics(editorImpl, highlighter).stringWidth(text)
        }

        fun getTextWidthWithHighlighters(editorImpl: EditorImpl,
                                         text: HighlightedTextUiWrapper): Int {
            val line = text.text
            val highlighters = text.highlighters
            val charArray = line.toCharArray()

            val lineHighlighters = getLinesHighlighters(listOf(line), highlighters)[0]
            assertNotNull(lineHighlighters, "lineHighlighters != null")

            var width = 0
            executeActionOverRanges(charArray, lineHighlighters) { range, model ->
                width += getTextWidth(editorImpl, charArray, range.from, range.to - 1, model)
            }

            return width
        }

        private fun getTextHeight(fontMetrics: FontMetrics) = fontMetrics.ascent + textHeightAdditionFactor

        fun getTextHeight(editorImpl: EditorImpl, highlighter: HighlighterUiModel?): Int {
            return getTextHeight(getFontMetrics(editorImpl, highlighter))
        }

        fun renderText(g: Graphics,
                       rect: Rectangle,
                       editorImpl: EditorImpl,
                       text: String,
                       delta: Int): Rectangle {
            val textHeight = getTextHeight(editorImpl, null)
            val adjustedRect = Rectangle(rect.x, rect.y + textHeight, rect.width, rect.height - textHeight)
            g.drawString(text, adjustedRect.x, adjustedRect.y)

            return Rectangle(adjustedRect.x, adjustedRect.y + delta, adjustedRect.width, adjustedRect.height - delta)
        }

        fun renderTextWithIcon(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               text: HighlightedTextUiWrapper,
                               icon: Icon,
                               gapBetweenTextAndIcon: Int,
                               delta: Int): Rectangle {
            val rectHeight = calculateTextHeightWithIcon(editorImpl, icon, text)
            val contextComponent = editorImpl.contentComponent
            icon.paintIcon(contextComponent, g, rect.x, rect.y)

            val adjustedRect = Rectangle(rect).apply {
                x += icon.iconWidth + gapBetweenTextAndIcon
            }

            renderLine(g, adjustedRect, editorImpl, text.text, text.highlighters, delta)
            val finalDelta = rectHeight + delta
            return Rectangle(rect).apply {
                y += finalDelta
                height -= finalDelta
            }
        }

        fun calculateTextHeightWithIcon(editorImpl: EditorImpl,
                                        icon: Icon,
                                        text: HighlightedTextUiWrapper): Int {
            val textHeight = getLineHeightWithHighlighters(editorImpl, text.highlighters)
            return max(icon.iconHeight, textHeight)
        }

        fun calculateWidthOfTextWithIcon(editorImpl: EditorImpl,
                                         icon: Icon,
                                         gapBetweenTextAndIcon: Int,
                                         text: HighlightedTextUiWrapper): Int {
            val textWidth = getTextWidthWithHighlighters(editorImpl, text)
            return icon.iconWidth + gapBetweenTextAndIcon + textWidth
        }

        fun renderLines(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        lines: List<String>,
                        delta: Int): Rectangle {
            val textHeight = getTextHeight(editorImpl, null)
            var textDelta = textHeight

            for (line in lines) {
                g.drawString(line, rect.x, rect.y + textDelta)
                textDelta += textHeight
            }

            val finalDelta = textDelta - textHeight + delta
            return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
        }

        fun renderLines(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        lines: List<String>,
                        highlighters: Collection<HighlighterUiModel>,
                        delta: Int): Rectangle {
            val linesHighlighters = getLinesHighlighters(lines, highlighters)
            var textDelta = 0

            for (i in lines.indices) {
                val currentHighlighters = linesHighlighters[i]
                assertNotNull(currentHighlighters, "linesHighlighters[i] != null")

                val lineHeight = getLineHeightWithHighlighters(editorImpl, currentHighlighters)
                textDelta += lineHeight

                val lineRect = Rectangle(rect).apply {
                    y += textDelta
                    height = lineHeight
                }

                renderLineWithHighlighters(g, lineRect, editorImpl, lines[i], currentHighlighters)
            }

            val finalDelta = textDelta + delta
            return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
        }

        fun renderLine(g: Graphics,
                       rect: Rectangle,
                       editorImpl: EditorImpl,
                       line: String,
                       highlighters: Collection<HighlighterUiModel>,
                       delta: Int): Rectangle {
            val rangesWithHighlighters = getLinesHighlighters(listOf(line), highlighters)[0]
            assertNotNull(rangesWithHighlighters, "rangesWithHighlighters != null")
            val lineHeight = getLineHeightWithHighlighters(editorImpl, rangesWithHighlighters)
            UpdatedRectCookie(rect, yDelta = lineHeight, heightDelta = lineHeight).use {
                renderLineWithHighlighters(g, rect, editorImpl, line, rangesWithHighlighters)
            }

            val finalDelta = lineHeight + delta
            return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
        }

        fun getLineHeightWithHighlighters(editorImpl: EditorImpl,
                                          rangesWithHighlighters: Collection<RangeWithHighlighter>): Int {
            return getLineHeightWithHighlighters(editorImpl, rangesWithHighlighters.map { it.highlighter })
        }

        fun getLineHeightWithHighlighters(editorImpl: EditorImpl,
                                          highlightersModels: List<HighlighterUiModel?>): Int {
            val heightWithoutHighlighter = getTextHeight(editorImpl, null)
            var maxHeight = heightWithoutHighlighter
            for (model in highlightersModels) {
                maxHeight = max(maxHeight, getTextHeight(editorImpl, model))
            }

            return maxHeight
        }

        private fun renderLineWithHighlighters(g: Graphics,
                                               rect: Rectangle,
                                               editorImpl: EditorImpl,
                                               line: String,
                                               rangesWithHighlighters: List<RangeWithHighlighter>) {
            var textLength = 0
            val chars = line.toCharArray()
            executeActionOverRanges(chars, rangesWithHighlighters) { range, model ->
                textLength += drawText(g, rect, editorImpl, chars, range, textLength, model)
            }
        }

        private fun executeActionOverRanges(line: CharArray,
                                            lineHighlighters: List<RangeWithHighlighter>,
                                            actionWithTextRange: (Range<Int>, HighlighterUiModel?) -> Unit) {
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

        private fun drawText(g: Graphics,
                             rect: Rectangle,
                             editorImpl: EditorImpl,
                             line: CharArray,
                             range: Range<Int>,
                             currentTextLength: Int,
                             highlighterModel: HighlighterUiModel?): Int {
            val from = range.from
            val to = range.to
            val metrics = getFontMetrics(editorImpl, highlighterModel)
            val textWidth = getTextWidth(editorImpl, line, from, to - 1, highlighterModel)
            val textHeight = getTextHeight(editorImpl, highlighterModel)

            if (highlighterModel == null) {
                g.drawChars(line, from, to - from, rect.x + currentTextLength, rect.y)
            } else {
                val backgroundStyle = highlighterModel.backgroundStyle
                if (backgroundStyle != null) {
                    UpdatedGraphicsCookie(g, color = backgroundStyle.backgroundColor).use {
                        val y = rect.y - textHeight + metrics.descent
                        if (backgroundStyle.roundedRect) {
                            g.fillRoundRect(rect.x, y, textWidth, textHeight, 3, 3)
                        } else {
                            g.fillRect(rect.x, y, textWidth, textHeight)
                        }
                    }
                }

                g.drawString(AttributedCharsIterator(line, from, to, highlighterModel), rect.x + currentTextLength, rect.y)
            }

            return textWidth
        }

        data class RangeWithHighlighter(val range: Range<Int>, val highlighter: HighlighterUiModel?)

        fun getLinesHighlighters(lines: List<String>,
                                 highlightersModels: Collection<HighlighterUiModel>): HashMap<Int, List<RangeWithHighlighter>> {
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
                    if (highlighterModel.startOffset <= lineEndOffset && highlighterModel.endOffset >= lineStartOffset) {
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

        fun createRectanglesForHighlighters(line: String,
                                            highlighters: Collection<HighlighterUiModel>,
                                            context: RectangleModelBuildContext) {
            val lines = mutableListOf(line)
            val linesHighlighters = getLinesHighlighters(lines, highlighters)
            createRectanglesForHighlighters(lines, linesHighlighters, context)
        }

        fun createRectanglesForHighlighters(lines: List<String>,
                                            linesHighlighters: HashMap<Int, List<RangeWithHighlighter>>,
                                            context: RectangleModelBuildContext) {
            val editorImpl = context.editorImpl
            val rect = context.rect
            var yDelta = 0
            val project = editorImpl.project

            assertNotNull(project, "editorImpl.project != null")
            for (i in lines.indices) {
                val chars = lines[i].toCharArray()
                val lineHighlighters = linesHighlighters[i]
                assertNotNull(lineHighlighters, "linesHighlighters[i] != null")

                var textLength = 0
                val lineHeight = getLineHeightWithHighlighters(editorImpl, lineHighlighters)
                executeActionOverRanges(chars, lineHighlighters) { range, model ->
                    val textWidth = getTextWidth(editorImpl, chars, range.from, range.to - 1, model)
                    if (model != null) {
                        val highlighterRect = Rectangle(rect.x + textLength, rect.y + yDelta, textWidth, lineHeight)
                        context.rectanglesModel.addElement(model, highlighterRect)
                    }

                    textLength += textWidth
                }

                yDelta += lineHeight
            }
        }

        fun addDeltaBetweenSections(rect: Rectangle) {
            addHeightDelta(rect, heightDeltaBetweenSections)
        }

        fun addHeightDelta(rect: Rectangle, delta: Int) {
            rect.y += delta
            rect.height -= delta
        }

        fun buildRectanglesModel(editorImpl: EditorImpl,
                                 intelligentComment: IntelligentCommentUiModel,
                                 xDelta: Int,
                                 yDelta: Int): RectanglesModel {
            val widthAndHeight = WidthAndHeight().apply {
                height = minCommentHeightPx
            }

            val initialRect = Rectangle(xDelta, yDelta, Int.MAX_VALUE, Int.MAX_VALUE)
            val model = RectanglesModel()
            val buildContext = RectangleModelBuildContext(model, widthAndHeight, initialRect, editorImpl)

            fun updateRectYAndHeight(delta: Int) {
                initialRect.y += delta
                widthAndHeight.height += delta
            }

            CommentAuthorsRenderer.getRendererFor(intelligentComment.authorsSection.content).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            SegmentsRenderer.getRendererFor(intelligentComment.contentSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            ReferencesRenderer.getRendererFor(intelligentComment.referencesSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            InvariantsRenderer.getRendererFor(intelligentComment.invariantsSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            ToDosRenderer.getRendererFor(intelligentComment.todosSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            model.addElement(intelligentComment, Rectangle(xDelta, yDelta, widthAndHeight.width, widthAndHeight.height))

            return model.apply {
                setSize(widthAndHeight.width, widthAndHeight.height)
                seal()
            }
        }

        fun updateHeightAndAddModel(renderer: Renderer,
                                    context: RectangleModelBuildContext,
                                    uiInteractionModel: UiInteractionModelBase) {
            val width = renderer.calculateExpectedWidthInPixels(context.editorImpl)
            val height = renderer.calculateExpectedHeightInPixels(context.editorImpl)

            context.widthAndHeight.height += height
            context.widthAndHeight.width = max(width, context.widthAndHeight.width)

            val rect = context.rect
            context.rectanglesModel.addElement(uiInteractionModel, Rectangle(rect.x, rect.y, width, height))
            rect.y += height
        }

        fun addHeightDeltaTo(widthAndHeight: WidthAndHeight, rect: Rectangle, delta: Int) {
            widthAndHeight.height += delta
            rect.y += delta
        }

        fun addHeightDeltaTo(context: RectangleModelBuildContext, delta: Int) {
            addHeightDeltaTo(context.widthAndHeight, context.rect, delta)
        }
    }
}