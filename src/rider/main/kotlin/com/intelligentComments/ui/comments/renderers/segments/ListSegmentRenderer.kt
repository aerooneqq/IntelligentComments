package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class ListSegmentRenderer(private val model: ListContentSegmentUiModel) : SegmentRenderer {
    companion object {
        private const val deltaBetweenListHeaderAndContent = 2
        const val leftIndentForListContent = 15
        const val bulletRadius = 6
    }


    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        var adjustedRect = renderListHeader(g, rect, editorImpl)
        var finalY = adjustedRect.y

        executeIfExpanded {
            UpdatedRectCookie(adjustedRect, xDelta = leftIndentForListContent).use {
                for (contentSegments in model.contentSegments) {
                    drawItemBullet(g, adjustedRect, editorImpl, contentSegments.content.first())

                    val segments = contentSegments.content
                    adjustedRect = ContentSegmentsUtil.renderSegments(segments, g, adjustedRect, editorImpl, rectanglesModel)
                    adjustedRect.y += ContentSegmentsUtil.deltaBetweenSegments
                }

                finalY = adjustedRect.y - ContentSegmentsUtil.deltaBetweenSegments
            }
        }

        return Rectangle(rect.x, finalY, rect.width, rect.height)
    }

    private fun executeIfExpanded(action: () -> Unit) {
        if (model.isExpanded) {
            action()
        }
    }

    private fun renderListHeader(g: Graphics,
                                 rect: Rectangle,
                                 editorImpl: EditorImpl): Rectangle {
        val header = model.header
        val headerText = header.textWrapper.text
        val headerHighlighters = header.textWrapper.highlighters
        val delta = if (model.isExpanded) deltaBetweenListHeaderAndContent else 0
        return TextUtil.renderLine(g, rect, editorImpl, headerText, headerHighlighters, delta)
    }

    private fun drawItemBullet(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               segment: ContentSegmentUiModel) {
        val fontMetrics = TextUtil.getFontMetrics(editorImpl, null)

        when (segment) {
            is ImageContentSegmentUiModel,
            is ListContentSegmentUiModel,
            is TextContentSegmentUiModel -> {
                val bulletColor = model.project.service<ColorsProvider>().getColorFor(Colors.ListItemBulletBackground)
                UpdatedGraphicsCookie(g, color = bulletColor).use {
                    g.fillOval(rect.x - 11, rect.y + fontMetrics.descent + fontMetrics.ascent / 4, bulletRadius, bulletRadius)
                }
            }
        }
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        var height = getHeaderHeightWithDelta(editorImpl)

        executeIfExpanded {
            for (contentSegments in model.contentSegments) {
                for (content in contentSegments.content) {
                    height += SegmentRenderer.getRendererFor(content).calculateExpectedHeightInPixels(editorImpl)
                    height += ContentSegmentsUtil.deltaBetweenSegments
                }
            }
        }

        return height - if (model.isExpanded) ContentSegmentsUtil.deltaBetweenSegments else 0
    }

    private fun getHeaderHeight(editorImpl: EditorImpl): Int {
        val highlighters = model.header.textWrapper.highlighters
        return TextUtil.getLineHeightWithHighlighters(editorImpl, highlighters)
    }

    private fun getHeaderHeightWithDelta(editorImpl: EditorImpl): Int {
        var height = getHeaderHeight(editorImpl)
        height += if (model.isExpanded) deltaBetweenListHeaderAndContent else 0
        return height
    }

    private fun getHeaderWidth(editorImpl: EditorImpl): Int {
        return TextUtil.getTextWidth(editorImpl, model.header.textWrapper.text)
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        var headerWidth = TextUtil.getTextWidth(editorImpl, model.header.textWrapper.text)

        executeIfExpanded {
            for (contentSegments in model.contentSegments) {
                for (content in contentSegments.content) {
                    val renderer = SegmentRenderer.getRendererFor(content)
                    val segmentWidth = renderer.calculateExpectedWidthInPixels(editorImpl)
                    headerWidth = max(segmentWidth + leftIndentForListContent, headerWidth)
                }
            }
        }

        return headerWidth
    }

    override fun accept(context: RectangleModelBuildContext) {
        aacceptHeaderModel(context)
        acceptHeaderTextHighlighters(context)
        acceptListItemsIfExpanded(context)
    }

    private fun aacceptHeaderModel(context: RectangleModelBuildContext) {
        val editorImpl = context.editorImpl
        val rect = context.rect
        val headerRect = Rectangle(rect.x, rect.y, getHeaderWidth(editorImpl), getHeaderHeight(editorImpl))
        context.rectanglesModel.addElement(model.header, headerRect)
    }

    private fun acceptHeaderTextHighlighters(context: RectangleModelBuildContext) {
        val headerText = model.header.textWrapper
        TextUtil.createRectanglesForHighlighters(headerText.text, headerText.highlighters, context)
    }

    private fun acceptListItemsIfExpanded(context: RectangleModelBuildContext) {
        val rect = context.rect
        val editorImpl = context.editorImpl

        executeIfExpanded {
            UpdatedRectCookie(rect, xDelta = leftIndentForListContent, yDelta = getHeaderHeightWithDelta(editorImpl)).use {
                for (contentSegments in model.contentSegments) {
                    for (content in contentSegments.content) {
                        val renderer = SegmentRenderer.getRendererFor(content)
                        val height = renderer.calculateExpectedHeightInPixels(editorImpl)
                        renderer.accept(context)
                        rect.y += height + ContentSegmentsUtil.deltaBetweenSegments
                    }
                }
            }
        }
    }
}