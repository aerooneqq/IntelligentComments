package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.ContentSegmentsUtil
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
    rectanglesModel: RectanglesModel
  ): Rectangle {
    UpdatedRectCookie(rect, yDelta = -2).use {
      renderHeader(g, rect, editorImpl, rectanglesModel)
    }

    val nameWidth = calculateHeaderWidth(editorImpl)
    val xDelta = nameWidth + deltaBetweenNameAndDescription
    val adjustedRect = Rectangle(rect).apply {
      x += xDelta
      y = rect.y
    }

    return ContentSegmentsUtil.renderSegments(content, g, adjustedRect, editorImpl, rectanglesModel).apply {
      x -= xDelta
      y += ContentSegmentsUtil.deltaBetweenSegments
    }
  }

  protected abstract fun calculateHeaderWidth(editorImpl: EditorImpl): Int
  protected abstract fun calculateHeaderHeight(editorImpl: EditorImpl): Int
  protected abstract fun renderHeader(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  )

  override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
    val nameHeight = calculateHeaderHeight(editorImpl)
    val contentHeight = ContentSegmentsUtil.calculateContentHeight(content, editorImpl) + ContentSegmentsUtil.deltaBetweenSegments
    return max(nameHeight, contentHeight)
  }

  override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
    var width = calculateHeaderWidth(editorImpl)
    width += deltaBetweenNameAndDescription
    width += ContentSegmentsUtil.calculateContentWidth(content, editorImpl)
    return width
  }

  override fun accept(context: RectangleModelBuildContext) {
  }
}