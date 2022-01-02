package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

abstract class LeftHeaderRightContentRenderer(
  private val content: Collection<ContentSegmentUiModel>
) : SegmentRenderer {
  companion object {
    private const val deltaBetweenNameAndDescription = 10
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    UpdatedRectCookie(rect).use {
      renderHeader(g, rect, editorImpl, rectanglesModel)
    }

    val xDelta = additionalRenderInfo.topmostLeftIndent + deltaBetweenNameAndDescription
    val adjustedRect = Rectangle(rect).apply {
      x += xDelta
      y = rect.y
    }

    return ContentSegmentsUtil.renderSegments(content, g, adjustedRect, editorImpl, rectanglesModel).apply {
      x -= xDelta
      y += ContentSegmentsUtil.deltaBetweenSegments
    }
  }

  fun calculateHeaderWidth(editorImpl: EditorImpl) = calculateHeaderWidthInternal(editorImpl)

  protected abstract fun calculateHeaderWidthInternal(editorImpl: EditorImpl): Int
  protected abstract fun calculateHeaderHeightInternal(editorImpl: EditorImpl): Int
  protected abstract fun renderHeader(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  )

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val nameHeight = calculateHeaderHeightInternal(editorImpl)
    val delta = ContentSegmentsUtil.deltaBetweenSegments
    val contentHeight = ContentSegmentsUtil.calculateContentHeight(content, editorImpl, additionalRenderInfo) + delta
    return max(nameHeight, contentHeight)
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var width = additionalRenderInfo.topmostLeftIndent
    width += deltaBetweenNameAndDescription
    width += ContentSegmentsUtil.calculateContentWidth(content, editorImpl, additionalRenderInfo)
    return width
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context.copyWithNewRect(Rectangle(context.rect).apply {
      x += deltaBetweenNameAndDescription + context.additionalRenderInfo.topmostLeftIndent
    }), content)
  }
}

open class LeftTextHeaderAndRightContentRenderer(
  private val header: HighlightedTextUiWrapper,
  private val content: Collection<ContentSegmentUiModel>
) : LeftHeaderRightContentRenderer(content) {
  override fun calculateHeaderWidthInternal(editorImpl: EditorImpl): Int {
    return TextUtil.getTextWidthWithHighlighters(editorImpl, header)
  }

  override fun calculateHeaderHeightInternal(editorImpl: EditorImpl): Int {
    return TextUtil.getLineHeightWithHighlighters(editorImpl, header.highlighters)
  }

  override fun renderHeader(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ) {
    val firstContentChildHasHeader = !content.isEmpty() && SegmentRenderer.getRendererFor(content.first()) is LeftHeaderRightContentRenderer
    val adjustedRect = if (header.highlighters.first().backgroundStyle != null && firstContentChildHasHeader) {
      Rectangle(rect).apply { y -= TextUtil.backgroundArcDimension }
    } else {
      rect
    }

    TextUtil.renderLine(g, adjustedRect, editorImpl, header, 0)
  }

  override fun accept(context: RectangleModelBuildContext) {
    TextUtil.createRectanglesForHighlightedText(header, context)
    super.accept(context)
  }
}