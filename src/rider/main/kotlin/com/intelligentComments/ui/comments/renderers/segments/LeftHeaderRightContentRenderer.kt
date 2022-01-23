package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.model.ModelWithContent
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.GroupedUiModel
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
  private val content: Collection<ContentSegmentUiModel>,
  private val renderHeader: Boolean = true
) : SegmentRenderer {
  companion object {
    private const val deltaBetweenFirstLevelHeaderAndSecondLevel = 15
    private const val deltaBetweenSecondLevelHeaderAndContent = 10
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

    val xDelta = additionalRenderInfo.topmostLeftIndent + calculateHeaderContentDelta()
    val adjustedRect = if (shouldDrawHeader) {
      Rectangle(rect).apply {
        x += xDelta
        y = rect.y
      }
    } else {
      rect
    }

    if (content.isEmpty()) {
      return Rectangle(rect).apply {
        if (shouldDrawHeader) {
          y += calculateHeaderHeightInternal(editorImpl)
        }
      }
    }

    ContentSegmentsUtil.renderSegments(content, g, adjustedRect, editorImpl, rectanglesModel).apply {
      x -= if (shouldDrawHeader) xDelta else 0
    }

    return Rectangle(rect).apply {
      y += calculateExpectedHeightInPixels(editorImpl, additionalRenderInfo)
    }
  }

  private fun calculateHeaderContentDelta(): Int {
    if (isSecondLevelHeader()) {
      return deltaBetweenSecondLevelHeaderAndContent
    }

    return deltaBetweenFirstLevelHeaderAndSecondLevel
  }

  private fun isSecondLevelHeader(): Boolean {
    val parent = getMeaningfulParent() ?: return false

    var headersCount = 0
    var current: UiInteractionModelBase? = parent
    while (current != null) {
      if (isHeader(current)) {
        ++headersCount
      }

      current = current.parent
    }

    return headersCount == 1
  }

  private fun isHeader(model: UiInteractionModelBase): Boolean {
    return model is ContentSegmentUiModel && SegmentRenderer.getRendererFor(model) is LeftHeaderRightContentRenderer
  }

  private fun getMeaningfulParent(): UiInteractionModelBase? {
    if (content.isEmpty()) return null
    return content.first().parent?.parent?.parent
  }

  protected fun shouldDrawHeader(editorImpl: EditorImpl): Boolean {
    if (!renderHeader) return false

    if (content.isEmpty()) {
      return true
    }

    val parent = getMeaningfulParent()
    var current = parent
    while (current != null) {
      if (current is ContentSegmentUiModel && SegmentRenderer.getRendererFor(current) is LeftHeaderRightContentRenderer) {
        return true
      }

      current = current.parent
    }

    val singleContent = parent is ModelWithContent && parent.contentSection.content.size == 1
    val settings = RiderIntelligentCommentsSettingsProvider.getInstance()
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

    val contentHeight = ContentSegmentsUtil.calculateContentHeight(content, editorImpl, additionalRenderInfo)

    return max(nameHeight, contentHeight)
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var width = 0

    if (shouldDrawHeader(editorImpl)) {
      width = additionalRenderInfo.topmostLeftIndent
      width += calculateHeaderContentDelta()
    }

    width += ContentSegmentsUtil.calculateContentWidth(content, editorImpl, additionalRenderInfo)
    return width
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context.createCopy(Rectangle(context.rect).apply {
      x += if (shouldDrawHeader(context.editorImpl)) {
        calculateHeaderContentDelta() + context.additionalRenderInfo.topmostLeftIndent
      } else {
        0
      }
    }), content)
  }
}

open class LeftTextHeaderAndRightContentRenderer : LeftHeaderRightContentRenderer {
  private val header: HighlightedTextUiWrapper


  constructor(
    header: HighlightedTextUiWrapper,
    content: Collection<ContentSegmentUiModel>,
    renderHeader: Boolean = true
  ) : super(content, renderHeader) {
    this.header = header
  }

  constructor(
    header: HighlightedTextUiWrapper,
    segments: ContentSegmentsUiModel,
    renderHeader: Boolean = true
  ) : this(header, segments.contentSection.content, renderHeader)


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
    if (shouldDrawHeader(context.editorImpl)) {
      TextUtil.createRectanglesForHighlightedText(header, context)
    }

    super.accept(context)
  }
}