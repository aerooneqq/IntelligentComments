package com.intelligentComments.ui

import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.CommentAuthorsRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.util.ui.UIUtil
import java.awt.*
import javax.swing.Icon
import kotlin.math.max

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

class WidthAndHeight {
    var width = 0
    var height = 0
}

class CommentsUtil {
    companion object {
        private val fontSize = UIUtil.getFontSize(UIUtil.FontSize.NORMAL)
        val font: Font = UIUtil.getLabelFont().deriveFont(Font.PLAIN, fontSize)
        private const val minCommentHeightPx = 0
        const val heightDeltaBetweenSections = 10
        const val deltaBetweenHeaderAndContent = 5
        const val deltaBetweenIconAndTextInHeader = 2
        const val textHeightAdditionFactor = 2

        fun getFontMetrics(editorImpl: EditorImpl): FontMetrics = editorImpl.contentComponent.getFontMetrics(font)
        fun getTextWidth(fontMetrics: FontMetrics, text: String) = fontMetrics.stringWidth(text)
        fun getTextWidth(editorImpl: EditorImpl, text: String) = getFontMetrics(editorImpl).stringWidth(text)
        fun getTextHeight(fontMetrics: FontMetrics) = fontMetrics.ascent + textHeightAdditionFactor
        fun getTextHeight(editorImpl: EditorImpl) = getTextHeight(getFontMetrics(editorImpl))
        fun getLineInterval(editorImpl: EditorImpl) = 0

        fun renderText(g: Graphics,
                       rect: Rectangle,
                       editorImpl: EditorImpl,
                       text: String,
                       delta: Int): Rectangle {
            val textHeight = getTextHeight(editorImpl)
            val adjustedRect = Rectangle(rect.x, rect.y + textHeight, rect.width, rect.height - textHeight)
            g.drawString(text, adjustedRect.x, adjustedRect.y)

            return Rectangle(adjustedRect.x, adjustedRect.y + delta, adjustedRect.width, adjustedRect.height - delta)
        }

        fun renderTextWithIcon(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               text: String,
                               icon: Icon,
                               gapBetweenTextAndIcon: Int,
                               delta: Int): Rectangle {
            val rectHeight = calculateTextHeightWithIcon(editorImpl, icon, text)
            val contextComponent = editorImpl.contentComponent
            icon.paintIcon(contextComponent, g, rect.x, rect.y)

            val adjustedRect = Rectangle(rect).apply {
                x += icon.iconWidth + gapBetweenTextAndIcon
                y -= 2
            }

            renderText(g, adjustedRect, editorImpl, text, delta)
            val finalDelta = rectHeight + delta
            return Rectangle(rect).apply {
                y += finalDelta
                height -= finalDelta
            }
        }

        fun calculateTextHeightWithIcon(editorImpl: EditorImpl,
                                        icon: Icon,
                                        text: String): Int {
            return max(icon.iconHeight, getTextHeight(editorImpl))
        }

        fun calculateWidthOfTextWithIcon(editorImpl: EditorImpl,
                                         icon: Icon,
                                         gapBetweenTextAndIcon: Int,
                                         text: String): Int {
            return icon.iconWidth + gapBetweenTextAndIcon + getTextWidth(editorImpl, text)
        }

        fun renderLines(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        lines: List<String>,
                        delta: Int): Rectangle {
            val textHeight = getTextHeight(editorImpl)
            var textDelta = textHeight

            for (line in lines) {
                g.drawString(line, rect.x, rect.y + textDelta)
                textDelta += textHeight
            }

            val finalDelta = textDelta - textHeight + delta
            return Rectangle(rect.x, rect.y + finalDelta, rect.width, rect.height - finalDelta)
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