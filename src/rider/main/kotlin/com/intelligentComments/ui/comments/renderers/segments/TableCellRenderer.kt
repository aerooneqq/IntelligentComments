package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.core.domain.core.HorizontalAlignment
import com.intelligentComments.core.domain.core.VerticalAlignment
import com.intelligentComments.ui.comments.model.content.table.TableCellUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

class TableCellRenderer(private val cell: TableCellUiModel) : Renderer, RectangleModelBuildContributor {
  companion object {
    const val cellMargin = 6
    const val deltaBetweenContentsInCell = 3
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    if (cell.properties.isHeader) {
      UpdatedGraphicsCookie(g, color = cell.backgroundColor).use {
        g.fillRect(rect.x, rect.y, rect.width, rect.height)
      }
    }

    val adjustedRect = calculateRectForCellContent(rect, editorImpl, additionalRenderInfo)
    return ContentSegmentsUtil.renderSegments(
      cell.contentSegments.contentSection.content,
      g,
      adjustedRect,
      editorImpl,
      rectanglesModel
    )
  }

  private fun calculateRectForCellContent(
    rect: Rectangle,
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val allContentHeight = calculateExpectedHeightInPixels(editorImpl, additionalRenderInfo)
    val allContentWidth = calculateExpectedWidthInPixels(editorImpl, additionalRenderInfo)
    val cellRectWidth = rect.width
    val cellRectHeight = rect.height

    val yDelta = when (cell.properties.verticalAlignment) {
      VerticalAlignment.CENTER -> (cellRectHeight - allContentHeight) / 2 + cellMargin
      VerticalAlignment.TOP -> cellMargin
      VerticalAlignment.BOTTOM -> cellRectHeight - allContentHeight + cellMargin
    }

    val xDelta = when (cell.properties.horizontalAlignment) {
      HorizontalAlignment.CENTER -> (cellRectWidth - allContentWidth) / 2 + cellMargin
      HorizontalAlignment.LEFT -> cellMargin
      HorizontalAlignment.RIGHT -> cellRectWidth - allContentWidth + cellMargin
    }

    return Rectangle(rect).apply {
      x += xDelta
      y += yDelta
    }
  }

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val height = ContentSegmentsUtil.calculateContentHeight(cell.contentSegments, editorImpl, additionalRenderInfo)
    return height + 2 * cellMargin
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val width = ContentSegmentsUtil.calculateContentWidth(cell.contentSegments, editorImpl, additionalRenderInfo)
    return width + 2 * cellMargin
  }

  override fun accept(context: RectangleModelBuildContext) {
    val rect = calculateRectForCellContent(context.rect, context.editorImpl, context.additionalRenderInfo)

    val editorImpl = context.editorImpl
    for (cellContent in cell.contentSegments.contentSection.content) {
      val renderer = SegmentRenderer.getRendererFor(cellContent)
      val width = renderer.calculateExpectedWidthInPixels(editorImpl, context.additionalRenderInfo)
      val height = renderer.calculateExpectedHeightInPixels(editorImpl, context.additionalRenderInfo)

      val cellSegmentContentRect = Rectangle(rect).apply {
        this.width = width
        this.height = height
      }

      context.rectanglesModel.addElement(cellContent, cellSegmentContentRect)
      renderer.accept(context.withRectangle(cellSegmentContentRect))

      rect.y += height + deltaBetweenContentsInCell
    }
  }
}