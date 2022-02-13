package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.table.TableCellUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class TableSegmentRenderer(private val table: TableContentSegmentUiModel) : SegmentRenderer {
  companion object {
    const val deltaBetweenCellsAndTableName = 3
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val rowsHeights = calculateRowsHeights(editor, additionalRenderInfo).toList()
    val colsWidths = calculateColsWidths(editor, additionalRenderInfo).toList()
    val cellsHeight = rowsHeights.sum()
    val cellsWidth = colsWidths.sum()
    val nameWidth = calculateNameWidth(editor)

    val rectForCells = Rectangle(rect).apply {
      x += if (nameWidth > cellsWidth) (nameWidth - cellsWidth) / 2 else 0
    }

    drawTableBorders(g, rectForCells, rowsHeights, colsWidths)
    drawCells(g, rectForCells, editor, rectanglesModel, rowsHeights, colsWidths)

    val rectForName = Rectangle(rect).apply {
      x += if (cellsWidth > nameWidth) (cellsWidth - nameWidth) / 2 else 0
      y += cellsHeight + deltaBetweenCellsAndTableName
    }

    drawTableName(g, rectForName, editor)

    return Rectangle(rect).apply {
      y += calculateExpectedHeightInPixels(editor, additionalRenderInfo)
    }
  }

  private fun drawTableBorders(
    g: Graphics,
    rect: Rectangle,
    rowsHeights: Collection<Int>,
    colsWidths: Collection<Int>
  ) {
    val height = rowsHeights.sum()
    val width = colsWidths.sum()

    val borderColor = table.project.service<ColorsProvider>().getColorFor(Colors.TableBorderBackgroundColor)
    UpdatedGraphicsCookie(g, color = borderColor).use {
      drawLines(g, rect, rowsHeights, width) { gr, r, delta, dim -> drawHorizontalLine(gr, r, delta, dim) }
      drawLines(g, rect, colsWidths, height) { gr, r, delta, dim -> drawVerticalLine(gr, r, delta, dim) }
    }
  }

  private fun drawCells(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    rowsHeights: List<Int>,
    colsWidths: List<Int>
  ) {
    executeActionOverCellsAndRectangles(rowsHeights, colsWidths, rect) { cell, cellRect ->
      TableCellRenderer(cell).render(g, cellRect, editor, rectanglesModel, RenderAdditionalInfo.emptyInstance)
    }
  }

  private fun drawTableName(
    g: Graphics,
    rect: Rectangle,
    editor: Editor
  ) {
    val headerText = table.headerUiModel?.highlightedTextUiWrapper ?: return

    val text = headerText.text
    val highlighters = headerText.highlighters
    TextUtil.renderLine(g, rect, editor, text, highlighters, 0)
  }

  private fun executeActionOverCellsAndRectangles(
    rowsHeights: List<Int>,
    colsWidths: List<Int>,
    rect: Rectangle,
    action: (TableCellUiModel, Rectangle) -> Unit
  ) {
    var currentXDelta: Int
    var currentYDelta = 0

    for (rowDeltaIdx in rowsHeights.indices) {
      val rowDelta = rowsHeights[rowDeltaIdx]
      currentXDelta = 0

      for (colDeltaIdx in colsWidths.indices) {
        val width = colsWidths[colDeltaIdx]
        val height = rowsHeights[rowDeltaIdx]

        val cellRect = Rectangle(rect).apply {
          x += currentXDelta
          y += currentYDelta
          this.width = width
          this.height = height
        }

        action(table.rows[rowDeltaIdx].cells[colDeltaIdx], cellRect)
        currentXDelta += colsWidths[colDeltaIdx]
      }

      currentYDelta += rowDelta
    }
  }

  private fun drawLines(
    g: Graphics,
    rect: Rectangle,
    deltas: Collection<Int>,
    dimension: Int,
    drawAction: (Graphics, Rectangle, Int, Int) -> Unit
  ) {
    var currentDelta = 0
    drawAction(g, rect, 0, dimension)
    for (delta in deltas) {
      currentDelta += delta
      drawAction(g, rect, currentDelta, dimension)
    }
  }

  private fun drawVerticalLine(g: Graphics, rect: Rectangle, xDelta: Int, height: Int) {
    g.drawLine(rect.x + xDelta, rect.y, rect.x + xDelta, rect.y + height)
  }

  private fun drawHorizontalLine(g: Graphics, rect: Rectangle, yDelta: Int, width: Int) {
    g.drawLine(rect.x, rect.y + yDelta, rect.x + width, rect.y + yDelta)
  }

  private fun calculateRowsHeights(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Collection<Int> {
    val cellsHeights = mutableListOf<Int>()
    for (row in table.rows) {
      var maxCellHeightInRow = 0
      for (cell in row.cells) {
        val cellHeight = TableCellRenderer(cell).calculateExpectedHeightInPixels(editor, additionalRenderInfo)
        maxCellHeightInRow = max(maxCellHeightInRow, cellHeight)
      }

      cellsHeights.add(maxCellHeightInRow)
    }

    return cellsHeights
  }

  private fun calculateColsWidths(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Collection<Int> {
    if (table.rows.isEmpty()) return listOf()

    val cellsCountInRow = table.rows[0].cells.size
    val cellWidths = mutableListOf<Int>()

    for (cellIndex in 0 until cellsCountInRow) {
      var maxCellWidthInCol = 0
      for (rowIndex in 0 until table.rows.size) {
        val cell = table.rows[rowIndex].cells[cellIndex]
        val cellWidth = TableCellRenderer(cell).calculateExpectedWidthInPixels(editor, additionalRenderInfo)
        maxCellWidthInCol = max(maxCellWidthInCol, cellWidth)
      }

      cellWidths.add(maxCellWidthInCol)
    }

    return cellWidths
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    var tableHeight = calculateCellsHeight(editor, additionalRenderInfo)
    tableHeight += calculateNameHeight(editor) + deltaBetweenCellsAndTableName
    return tableHeight
  }

  private fun calculateCellsHeight(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ) = calculateRowsHeights(editor, additionalRenderInfo).sum()

  private fun calculateNameHeight(editor: Editor): Int {
    val headerText = table.headerUiModel?.highlightedTextUiWrapper ?: return 0

    val highlighters = headerText.highlighters
    return TextUtil.getLineHeightWithHighlighters(editor, highlighters)
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val tableCellsWidth = calculateCellsWidth(editor, additionalRenderInfo)
    val tableNameWidth = calculateNameWidth(editor)
    return max(tableCellsWidth, tableNameWidth)
  }

  private fun calculateCellsWidth(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo) = calculateColsWidths(editor, additionalRenderInfo).sum()

  private fun calculateNameWidth(editor: Editor): Int {
    val headerText = table.headerUiModel?.highlightedTextUiWrapper ?: return 0
    return TextUtil.getTextWidthWithHighlighters(editor, headerText)
  }

  override fun accept(context: RectangleModelBuildContext) {
    val rowsHeights = calculateRowsHeights(context.editor, context.additionalRenderInfo).toList()
    val colsWidths = calculateColsWidths(context.editor, context.additionalRenderInfo).toList()

    executeActionOverCellsAndRectangles(rowsHeights, colsWidths, context.rect) { cell, cellRect ->
      TableCellRenderer(cell).accept(context.withRectangle(cellRect))
    }
  }
}