package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.UpdatedGraphicsCookie
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.TableCellUiModel
import com.intelligentComments.ui.comments.model.TableContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class TableSegmentRenderer(private val table: TableContentSegmentUiModel) : SegmentRenderer {
    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        val rowsHeights = calculateRowsHeights(editorImpl)
        val colsWidths = calculateColsWidths(editorImpl)

        drawTableBorders(g, rect, rowsHeights, colsWidths)
        drawCells(g, rect, editorImpl, rectanglesModel, rowsHeights.toList(), colsWidths.toList())

        return Rectangle(rect).apply {
            y += calculateRowsHeights(editorImpl).sum()
        }
    }

    private fun drawTableBorders(g: Graphics,
                                 rect: Rectangle,
                                 rowsHeights: Collection<Int>,
                                 colsWidths: Collection<Int>) {
        val height = rowsHeights.sum()
        val width = colsWidths.sum()

        val borderColor = table.project.service<ColorsProvider>().getColorFor(Colors.TableBorderBackground)
        UpdatedGraphicsCookie(g, color = borderColor).use {
            drawLines(g, rect, rowsHeights, width) { gr, r, delta, dim -> drawHorizontalLine(gr, r, delta, dim) }
            drawLines(g, rect, colsWidths, height) { gr, r, delta, dim -> drawVerticalLine(gr, r, delta, dim) }
        }
    }

    private fun drawCells(g: Graphics,
                          rect: Rectangle,
                          editorImpl: EditorImpl,
                          rectanglesModel: RectanglesModel,
                          rowsHeights: List<Int>,
                          colsWidths: List<Int>) {
        executeActionOverCellsAndRectangles(rowsHeights, colsWidths, rect) { cell, cellRect ->
            TableCellRenderer(cell).render(g, cellRect, editorImpl, rectanglesModel)
        }
    }

    private fun executeActionOverCellsAndRectangles(rowsHeights: List<Int>,
                                                    colsWidths: List<Int>,
                                                    rect: Rectangle,
                                                    action: (TableCellUiModel, Rectangle) -> Unit) {
        var currentXDelta: Int
        var currentYDelta = 0

        for (rowDeltaIdx in rowsHeights.indices) {
            val rowDelta = rowsHeights[rowDeltaIdx]
            currentXDelta = 0

            for (colDeltaIdx in colsWidths.indices) {
                val cellRect = Rectangle(rect).apply {
                    x += currentXDelta
                    y += currentYDelta
                }

                action(table.rows[rowDeltaIdx].cells[colDeltaIdx], cellRect)
                currentXDelta += colsWidths[colDeltaIdx]
            }

            currentYDelta += rowDelta
        }
    }

    private fun drawLines(g: Graphics,
                          rect: Rectangle,
                          deltas: Collection<Int>,
                          dimension: Int,
                          drawAction: (Graphics, Rectangle, Int, Int) -> Unit) {
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

    private fun calculateRowsHeights(editorImpl: EditorImpl): Collection<Int> {
        val cellsHeights = mutableListOf<Int>()
        for (row in table.rows) {
            var maxCellHeightInRow = 0
            for (cell in row.cells) {
                val cellHeight = TableCellRenderer(cell).calculateExpectedHeightInPixels(editorImpl)
                maxCellHeightInRow = max(maxCellHeightInRow, cellHeight)
            }

            cellsHeights.add(maxCellHeightInRow)
        }

        return cellsHeights
    }

    private fun calculateColsWidths(editorImpl: EditorImpl): Collection<Int> {
        if (table.rows.isEmpty()) return listOf()

        val cellsCountInRow = table.rows[0].cells.size
        val cellWidths = mutableListOf<Int>()

        for (cellIndex in 0 until cellsCountInRow) {
            var maxCellWidthInCol = 0
            for (rowIndex in 0 until table.rows.size) {
                val cellWidth = TableCellRenderer(table.rows[rowIndex].cells[cellIndex]).calculateExpectedWidthInPixels(editorImpl)
                maxCellWidthInCol = max(maxCellWidthInCol, cellWidth)
            }

            cellWidths.add(maxCellWidthInCol)
        }

        return cellWidths
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        return calculateRowsHeights(editorImpl).sum()
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        return calculateColsWidths(editorImpl).sum()
    }

    override fun accept(context: RectangleModelBuildContext) {
        val rowsHeights = calculateRowsHeights(context.editorImpl).toList()
        val colsWidths = calculateColsWidths(context.editorImpl).toList()

        executeActionOverCellsAndRectangles(rowsHeights, colsWidths, context.rect) { cell, cellRect ->
            val newContext = RectangleModelBuildContext(context.rectanglesModel, context.widthAndHeight, cellRect, context.editorImpl)
            TableCellRenderer(cell).accept(newContext)
        }
    }
}