package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.core.domain.core.ListSegmentKind
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListItemUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class ListSegmentRenderer(private val model: ListContentSegmentUiModel) : SegmentRenderer {
  companion object {
    private const val deltaBetweenListHeaderAndContent = 2
    const val leftIndentForListContent = 15
    const val bulletRadius = 6
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect = renderListHeader(g, rect, editorImpl)
    var finalY = adjustedRect.y

    executeIfExpanded {
      UpdatedRectCookie(adjustedRect, xDelta = leftIndentForListContent).use {
        for ((index, item) in model.items.withIndex()) {
          val listContent = getAllContentFrom(item)
          drawItemBullet(g, adjustedRect, editorImpl, listContent.first(), index + 1)
          adjustedRect = ContentSegmentsUtil.renderSegments(listContent, g, adjustedRect, editorImpl, rectanglesModel)
          adjustedRect.y += ContentSegmentsUtil.deltaBetweenSegments
        }

        finalY = adjustedRect.y - ContentSegmentsUtil.deltaBetweenSegments
      }
    }

    return Rectangle(rect.x, finalY, rect.width, rect.height)
  }

  private fun getAllContentFrom(item: ListItemUiModel): Collection<ContentSegmentUiModel> {
    val header = item.header
    val description = item.description

    val listContent = mutableListOf<ContentSegmentUiModel>()
    if (header != null) listContent.addAll(header.content)
    if (description != null) listContent.addAll(description.content)
    return listContent
  }

  private fun executeIfExpanded(action: () -> Unit) {
    if (model.isExpanded) {
      action()
    }
  }

  private fun renderListHeader(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl
  ): Rectangle {
    val header = model.headerUiModel
    if (header != null) {
      val headerText = header.textWrapper.text
      val headerHighlighters = header.textWrapper.highlighters
      val delta = if (model.isExpanded) deltaBetweenListHeaderAndContent else 0
      return TextUtil.renderLine(g, rect, editorImpl, headerText, headerHighlighters, delta)
    }

    return rect
  }

  private fun drawItemBullet(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    segment: ContentSegmentUiModel,
    index: Int
  ) {
    val fontMetrics = TextUtil.getFontMetrics(editorImpl, null)

    when (segment) {
      is ImageContentSegmentUiModel,
      is ListContentSegmentUiModel,
      is TextContentSegmentUiModel -> {
        if (model.listKind == ListSegmentKind.Bullet) {
          drawBullet(g, rect, fontMetrics)
        } else if (model.listKind == ListSegmentKind.Number) {
          drawNumber(g, rect, editorImpl, index)
        }
      }
    }
  }

  private fun drawBullet(
    g: Graphics,
    rect: Rectangle,
    fontMetrics: FontMetrics
  ) {
    val bulletColor = model.project.service<ColorsProvider>().getColorFor(Colors.ListItemBulletBackgroundColor)
    UpdatedGraphicsCookie(g, color = bulletColor).use {
      g.fillOval(rect.x - 11, rect.y + fontMetrics.descent + fontMetrics.ascent / 4, bulletRadius, bulletRadius)
    }
  }

  private fun drawNumber(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    index: Int
  ) {
    val rectForNumber = Rectangle(rect).apply {
      x -= 14
    }

    val numberString = "$index."
    TextUtil.renderText(g, rectForNumber, editorImpl, numberString, 0)
  }

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var height = getHeaderHeightWithDelta(editorImpl)

    executeIfExpanded {
      for (item in model.items) {
        for (content in getAllContentFrom(item)) {
          height += SegmentRenderer.getRendererFor(content).calculateExpectedHeightInPixels(editorImpl, additionalRenderInfo)
          height += ContentSegmentsUtil.deltaBetweenSegments
        }
      }
    }

    return height - if (model.isExpanded) ContentSegmentsUtil.deltaBetweenSegments else 0
  }

  private fun getHeaderHeight(editorImpl: EditorImpl): Int {
    val header = model.headerUiModel ?: return 0

    val highlighters = header.textWrapper.highlighters
    return TextUtil.getLineHeightWithHighlighters(editorImpl, highlighters)
  }

  private fun getHeaderHeightWithDelta(editorImpl: EditorImpl): Int {
    if (model.headerUiModel == null) return 0

    var height = getHeaderHeight(editorImpl)
    height += if (model.isExpanded) deltaBetweenListHeaderAndContent else 0
    return height
  }

  private fun getHeaderWidth(editorImpl: EditorImpl): Int {
    if (model.headerUiModel == null) return 0

    return TextUtil.getTextWidth(editorImpl, model.headerUiModel.textWrapper.text)
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var headerWidth = getHeaderWidth(editorImpl)

    executeIfExpanded {
      for (item in model.items) {
        for (content in getAllContentFrom(item)) {
          val renderer = SegmentRenderer.getRendererFor(content)
          val segmentWidth = renderer.calculateExpectedWidthInPixels(editorImpl, additionalRenderInfo)
          headerWidth = max(segmentWidth + leftIndentForListContent, headerWidth)
        }
      }
    }

    return headerWidth
  }

  override fun accept(context: RectangleModelBuildContext) {
    if (model.headerUiModel != null) {
      acceptHeaderModel(context)
      acceptHeaderTextHighlighters(context)
    }

    acceptListItemsIfExpanded(context)
  }

  private fun acceptHeaderModel(context: RectangleModelBuildContext) {
    if (model.headerUiModel == null) return

    val editorImpl = context.editorImpl
    val rect = context.rect
    val headerRect = Rectangle(rect.x, rect.y, getHeaderWidth(editorImpl), getHeaderHeight(editorImpl))
    context.rectanglesModel.addElement(model.headerUiModel, headerRect)
  }

  private fun acceptHeaderTextHighlighters(context: RectangleModelBuildContext) {
    if (model.headerUiModel == null) return

    val headerText = model.headerUiModel.textWrapper
    TextUtil.createRectanglesForHighlighters(headerText.text, headerText.highlighters, context)
  }

  private fun acceptListItemsIfExpanded(context: RectangleModelBuildContext) {
    val rect = context.rect
    val editorImpl = context.editorImpl

    executeIfExpanded {
      UpdatedRectCookie(rect, xDelta = leftIndentForListContent, yDelta = getHeaderHeightWithDelta(editorImpl)).use {
        for (item in model.items) {
          for (content in getAllContentFrom(item)) {
            val renderer = SegmentRenderer.getRendererFor(content)
            val height = renderer.calculateExpectedHeightInPixels(editorImpl, context.additionalRenderInfo)
            renderer.accept(context)
            rect.y += height + ContentSegmentsUtil.deltaBetweenSegments
          }
        }
      }
    }
  }
}