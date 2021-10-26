package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.TableCellUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

class TableCellRenderer(private val cell: TableCellUiModel) : Renderer, RectangleModelBuildContributor {
    companion object {
        const val cellMargin = 6
        const val deltaBetweenContentsInCell = 3
    }

    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        var adjustedRect = rect.apply {
            x += cellMargin
            y += cellMargin
        }

        for (cellContent in cell.contentSegments.content) {
            adjustedRect = SegmentRenderer.getRendererFor(cellContent).render(g, adjustedRect, editorImpl, rectanglesModel)
            adjustedRect.y += deltaBetweenContentsInCell
        }

        return adjustedRect.apply {
            y -= deltaBetweenContentsInCell
        }
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        var height = 0
        for (cellContent in cell.contentSegments.content) {
            height += SegmentRenderer.getRendererFor(cellContent).calculateExpectedHeightInPixels(editorImpl)
            height += deltaBetweenContentsInCell
        }

        height -= deltaBetweenContentsInCell
        return height + 2 * cellMargin
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        var width = 0
        for (cellContent in cell.contentSegments.content) {
            val cellContentWidth = SegmentRenderer.getRendererFor(cellContent).calculateExpectedWidthInPixels(editorImpl)
            width = max(width, cellContentWidth)
        }

        return width + 2 * cellMargin
    }

    override fun accept(context: RectangleModelBuildContext) {
        val rect = Rectangle(context.rect).apply {
            x += cellMargin
            y += cellMargin
        }

        val editorImpl = context.editorImpl
        for (cellContent in cell.contentSegments.content) {
            val renderer = SegmentRenderer.getRendererFor(cellContent)
            val width = renderer.calculateExpectedWidthInPixels(editorImpl)
            val height = renderer.calculateExpectedHeightInPixels(editorImpl)

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