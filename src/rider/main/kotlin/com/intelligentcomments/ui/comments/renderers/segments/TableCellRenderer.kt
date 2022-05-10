package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.core.domain.core.HorizontalAlignment
import com.intelligentcomments.core.domain.core.VerticalAlignment
import com.intelligentcomments.ui.comments.model.content.table.TableCellUiModel
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectangleModelBuildContributor
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.ContentSegmentsUtil
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intelligentcomments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.editor.Editor
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
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    if (cell.properties.isHeader) {
      UpdatedGraphicsCookie(g, color = cell.backgroundColor).use {
        g.fillRect(rect.x, rect.y, rect.width, rect.height)
      }
    }

    val adjustedRect = calculateRectForCellContent(rect, editor, additionalRenderInfo)
    return ContentSegmentsUtil.renderSegments(
      cell.contentSegments.contentSection.content,
      g,
      adjustedRect,
      editor,
      rectanglesModel
    )
  }

  private fun calculateRectForCellContent(
    rect: Rectangle,
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val allContentHeight = calculateExpectedHeightInPixels(editor, additionalRenderInfo)
    val allContentWidth = calculateExpectedWidthInPixels(editor, additionalRenderInfo)
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
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val height = ContentSegmentsUtil.calculateContentHeight(cell.contentSegments, editor, additionalRenderInfo)
    return height + 2 * cellMargin
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val width = ContentSegmentsUtil.calculateContentWidth(cell.contentSegments, editor, additionalRenderInfo)
    return width + 2 * cellMargin
  }

  override fun accept(context: RectangleModelBuildContext) {
    val rect = calculateRectForCellContent(context.rect, context.editor, context.additionalRenderInfo)

    val editor = context.editor
    for (cellContent in cell.contentSegments.contentSection.content) {
      val renderer = cellContent.createRenderer()
      val width = renderer.calculateExpectedWidthInPixels(editor, context.additionalRenderInfo)
      val height = renderer.calculateExpectedHeightInPixels(editor, context.additionalRenderInfo)

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