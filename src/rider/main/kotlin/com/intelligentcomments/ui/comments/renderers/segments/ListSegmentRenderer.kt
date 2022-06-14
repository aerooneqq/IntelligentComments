package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.core.domain.core.CommonsHighlightersFactory
import com.intelligentcomments.core.domain.core.ListSegmentKind
import com.intelligentcomments.ui.colors.Colors
import com.intelligentcomments.ui.colors.ColorsProvider
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.list.ListItemUiModel
import com.intelligentcomments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.util.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.use
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class ListSegmentRenderer(private val model: ListContentSegmentUiModel) : SegmentRenderer {
  companion object {
    private const val deltaBetweenListHeaderAndContent = 2
    const val leftIndentForListContent = 15
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect = renderListHeader(g, rect, editor)
    var finalY = adjustedRect.y

    executeIfExpanded {
      UpdatedRectCookie(adjustedRect, xDelta = leftIndentForListContent).use {
        for ((index, item) in model.items.withIndex()) {
          val listContent = getAllContentFrom(item)
          if (listContent.isEmpty()) continue

          drawItemBullet(g, adjustedRect, editor, listContent.first(), index + 1)
          adjustedRect = ContentSegmentsUtil.renderSegments(listContent, g, adjustedRect, editor, rectanglesModel)
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
    if (header != null) listContent.addAll(header.contentSection.content)
    if (description != null) listContent.addAll(description.contentSection.content)
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
    editor: Editor
  ): Rectangle {
    val header = model.headerUiModel
    if (header != null) {
      val headerText = header.textWrapper.text
      val headerHighlighters = header.textWrapper.highlighters
      val delta = if (model.isExpanded) deltaBetweenListHeaderAndContent else 0
      return TextUtil.renderLine(g, rect, editor, headerText, headerHighlighters, delta)
    }

    return rect
  }

  private fun drawItemBullet(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    segment: ContentSegmentUiModel,
    index: Int
  ) {
    val fontMetrics = TextUtil.getFontMetrics(editor, null)

    when (segment) {
      is ImageContentSegmentUiModel,
      is ListContentSegmentUiModel,
      is TextContentSegmentUiModel -> {
        if (model.listKind == ListSegmentKind.Bullet) {
          drawBullet(g, editor, rect, fontMetrics)
        } else if (model.listKind == ListSegmentKind.Number) {
          drawNumber(g, rect, editor, index)
        }
      }
    }
  }

  private fun drawBullet(
    g: Graphics,
    editor: Editor,
    rect: Rectangle,
    fontMetrics: FontMetrics
  ) {
    val docCommentColor = CommonsHighlightersFactory.tryCreateCommentHighlighter(null, 1)?.textColor
    val bulletColor = docCommentColor ?: model.project.service<ColorsProvider>().getColorFor(Colors.ListItemBulletBackgroundColor)
    val textHeight = TextUtil.getTextHeight(editor, null)
    val bulletRadius = textHeight / 2
    UpdatedGraphicsCookie(g, color = bulletColor).use {
      g.fillOval(rect.x - 5 * bulletRadius / 3 , rect.y + bulletRadius / 2 + fontMetrics.descent / 2 - 1, bulletRadius, bulletRadius)
    }
  }

  private fun drawNumber(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    index: Int
  ) {
    val rectForNumber = Rectangle(rect).apply {
      x -= 14
    }

    val numberString = "$index."
    TextUtil.renderText(g, rectForNumber, editor, numberString, 0)
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var height = getHeaderHeightWithDelta(editor)

    executeIfExpanded {
      for (item in model.items) {
        for (content in getAllContentFrom(item)) {
          height += content.createRenderer().calculateExpectedHeightInPixels(editor, additionalRenderInfo)
          height += ContentSegmentsUtil.deltaBetweenSegments
        }
      }
    }

    return height - if (model.isExpanded) ContentSegmentsUtil.deltaBetweenSegments else 0
  }

  private fun getHeaderHeight(editor: Editor): Int {
    val header = model.headerUiModel ?: return 0

    val highlighters = header.textWrapper.highlighters
    return TextUtil.getLineHeightWithHighlighters(editor, highlighters)
  }

  private fun getHeaderHeightWithDelta(editor: Editor): Int {
    if (model.headerUiModel == null) return 0

    var height = getHeaderHeight(editor)
    height += if (model.isExpanded) deltaBetweenListHeaderAndContent else 0
    return height
  }

  private fun getHeaderWidth(editor: Editor): Int {
    if (model.headerUiModel == null) return 0

    return TextUtil.getTextWidth(editor, model.headerUiModel.textWrapper.text)
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var headerWidth = getHeaderWidth(editor)

    executeIfExpanded {
      for (item in model.items) {
        for (content in getAllContentFrom(item)) {
          val renderer = content.createRenderer()
          val segmentWidth = renderer.calculateExpectedWidthInPixels(editor, additionalRenderInfo)
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

    val editor = context.editor
    val rect = context.rect
    val headerRect = Rectangle(rect.x, rect.y, getHeaderWidth(editor), getHeaderHeight(editor))
    context.rectanglesModel.addElement(model.headerUiModel, headerRect)
  }

  private fun acceptHeaderTextHighlighters(context: RectangleModelBuildContext) {
    if (model.headerUiModel == null) return

    val headerText = model.headerUiModel.textWrapper
    TextUtil.createRectanglesForHighlighters(headerText.text, headerText.highlighters, context)
  }

  private fun acceptListItemsIfExpanded(context: RectangleModelBuildContext) {
    val rect = context.rect
    val editor = context.editor

    executeIfExpanded {
      UpdatedRectCookie(rect, xDelta = leftIndentForListContent, yDelta = getHeaderHeightWithDelta(editor)).use {
        for (item in model.items) {
          for (content in getAllContentFrom(item)) {
            val renderer = content.createRenderer()
            val height = renderer.calculateExpectedHeightInPixels(editor, context.additionalRenderInfo)
            renderer.accept(context)
            rect.y += height + ContentSegmentsUtil.deltaBetweenSegments
          }
        }
      }
    }
  }
}