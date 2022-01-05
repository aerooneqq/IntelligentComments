package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.model.ModelWithContent
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.GroupedUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.components.service
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
    val shouldDrawHeader = shouldDrawHeader(editorImpl)
    if (shouldDrawHeader) {
      UpdatedRectCookie(rect).use {
        renderHeader(g, rect, editorImpl, rectanglesModel)
      }
    }

    val xDelta = additionalRenderInfo.topmostLeftIndent + deltaBetweenNameAndDescription
    val adjustedRect = if (shouldDrawHeader) {
      Rectangle(rect).apply {
        x += xDelta
        y = rect.y
      }
    } else {
      rect
    }

    return ContentSegmentsUtil.renderSegments(content, g, adjustedRect, editorImpl, rectanglesModel).apply {
      x -= if (shouldDrawHeader) xDelta else 0
      y += ContentSegmentsUtil.deltaBetweenSegments
    }
  }

  private fun shouldDrawHeader(editorImpl: EditorImpl): Boolean {
    if (content.isEmpty()) {
      return true
    }

    val parent = content.first().parent?.parent?.parent
    var current = parent
    while (current != null) {
      if (current is ContentSegmentUiModel && SegmentRenderer.getRendererFor(current) is LeftHeaderRightContentRenderer) {
        return true
      }

      current = current.parent
    }

    val singleContent = parent is ModelWithContent && parent.content.size == 1
    val settings = editorImpl.project?.service<RiderIntelligentCommentsSettingsProvider>()
    val canOmitHeader = !(settings?.showFirstLevelHeaderWhenOneElement?.value ?: return true)

    return !(canOmitHeader && singleContent)
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
    val nameHeight = if (shouldDrawHeader(editorImpl)) calculateHeaderHeightInternal(editorImpl) else 0

    val delta = ContentSegmentsUtil.deltaBetweenSegments
    val contentHeight = ContentSegmentsUtil.calculateContentHeight(content, editorImpl, additionalRenderInfo) + delta

    return max(nameHeight, contentHeight)
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var width = 0

    if (shouldDrawHeader(editorImpl)) {
      width = additionalRenderInfo.topmostLeftIndent
      width += deltaBetweenNameAndDescription
    }

    width += ContentSegmentsUtil.calculateContentWidth(content, editorImpl, additionalRenderInfo)
    return width
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context.createCopy(Rectangle(context.rect).apply {
      x += if (shouldDrawHeader(context.editorImpl)) {
        deltaBetweenNameAndDescription + context.additionalRenderInfo.topmostLeftIndent
      } else {
        0
      }
    }), content)
  }
}

open class LeftTextHeaderAndRightContentRenderer(
  private val header: HighlightedTextUiWrapper,
  content: Collection<ContentSegmentUiModel>
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
    val adjustedRect = if (!shouldShiftUpHeader()) {
      rect
    } else {
      Rectangle(rect).apply { y -= TextUtil.backgroundArcDimension }
    }

    TextUtil.renderLine(g, adjustedRect, editorImpl, header, 0)
  }

  private fun shouldShiftUpHeader(): Boolean {
    val parent = header.parent?.parent?.parent
    return !(parent is GroupedUiModel && SegmentRenderer.getRendererFor(parent) is LeftHeaderRightContentRenderer)
  }

  override fun accept(context: RectangleModelBuildContext) {
    TextUtil.createRectanglesForHighlightedText(header, context)
    super.accept(context)
  }
}