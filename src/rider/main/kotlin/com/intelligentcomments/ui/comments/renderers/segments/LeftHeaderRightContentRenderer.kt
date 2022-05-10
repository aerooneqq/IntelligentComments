package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentcomments.ui.comments.model.ModelWithContent
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.util.ContentSegmentsUtil
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intelligentcomments.ui.util.TextUtil
import com.intelligentcomments.ui.util.UpdatedRectCookie
import com.intellij.openapi.editor.Editor
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
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val shouldDrawHeader = shouldDrawHeader()
    if (shouldDrawHeader) {
      UpdatedRectCookie(rect).use {
        renderHeader(g, rect, editor, rectanglesModel)
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
          y += calculateHeaderHeightInternal(editor)
        }
      }
    }

    ContentSegmentsUtil.renderSegments(content, g, adjustedRect, editor, rectanglesModel).apply {
      x -= if (shouldDrawHeader) xDelta else 0
    }

    return Rectangle(rect).apply {
      y += calculateExpectedHeightInPixels(editor, additionalRenderInfo)
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
    return model is ContentSegmentUiModel && model.createRenderer() is LeftHeaderRightContentRenderer
  }

  private fun getMeaningfulParent(): UiInteractionModelBase? {
    if (content.isEmpty()) return null
    return content.first().parent?.parent?.parent
  }

  protected fun shouldDrawHeader(): Boolean {
    if (!renderHeader) return false

    if (content.isEmpty()) {
      return true
    }

    val parent = getMeaningfulParent()
    var current = parent
    while (current != null) {
      if (current is ContentSegmentUiModel && current.createRenderer() is LeftHeaderRightContentRenderer) {
        return true
      }

      current = current.parent
    }

    val singleContent = parent is ModelWithContent && parent.contentSection.content.size == 1
    val settings = RiderIntelligentCommentsSettingsProvider.getInstance()
    val canOmitHeader = !settings.showFirstLevelHeaderWhenOneElement.value

    return !(canOmitHeader && singleContent)
  }

  fun calculateHeaderWidth(editor: Editor) = calculateHeaderWidthInternal(editor)

  protected abstract fun calculateHeaderWidthInternal(editor: Editor): Int
  protected abstract fun calculateHeaderHeightInternal(editor: Editor): Int
  protected abstract fun renderHeader(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel
  )

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val nameHeight = if (shouldDrawHeader()) calculateHeaderHeightInternal(editor) else 0
    val contentHeight = ContentSegmentsUtil.calculateContentHeight(content, editor, additionalRenderInfo)

    return max(nameHeight, contentHeight)
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var width = 0

    if (shouldDrawHeader()) {
      width = additionalRenderInfo.topmostLeftIndent
      width += calculateHeaderContentDelta()
      width += calculateHeaderHeightInternal(editor)
    }

    width += ContentSegmentsUtil.calculateContentWidth(content, editor, additionalRenderInfo)
    return width
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context.createCopy(Rectangle(context.rect).apply {
      x += if (shouldDrawHeader()) {
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


  override fun calculateHeaderWidthInternal(editor: Editor): Int {
    return TextUtil.getTextWidthWithHighlighters(editor, header)
  }

  override fun calculateHeaderHeightInternal(editor: Editor): Int {
    return TextUtil.getLineHeightWithHighlighters(editor, header.highlighters)
  }

  override fun renderHeader(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel
  ) {
    val adjustedRect = if (!shouldShiftUpHeader()) {
      rect
    } else {
      Rectangle(rect).apply { y -= TextUtil.backgroundArcDimension }
    }

    TextUtil.renderLine(g, adjustedRect, editor, header, 0)
  }

  private fun shouldShiftUpHeader(): Boolean {
    return false
  }

  override fun accept(context: RectangleModelBuildContext) {
    if (shouldDrawHeader()) {
      TextUtil.createRectanglesForHighlightedText(header, context)
    }

    super.accept(context)
  }
}