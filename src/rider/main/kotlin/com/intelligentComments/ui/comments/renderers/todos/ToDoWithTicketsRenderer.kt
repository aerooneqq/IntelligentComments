package com.intelligentComments.ui.comments.renderers.todos

import com.intelligentComments.ui.comments.model.todo.ToDoUiModel
import com.intelligentComments.ui.comments.model.todo.ToDoWithTicketsUiModel
import com.intelligentComments.ui.comments.renderers.ExpandableContentWithHeader
import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

interface ToDoRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(todo: ToDoUiModel): ToDoRenderer {
            return when(todo) {
                is ToDoWithTicketsUiModel -> ToDoWithTicketsRenderer(todo)
                else -> throw IllegalArgumentException(todo.toString())
            }
        }
    }
}

class ToDoWithTicketsRenderer(private val todo: ToDoWithTicketsUiModel) : ExpandableContentWithHeader(todo.headerUiModel), ToDoRenderer {
    companion object {
        const val deltaBetweenToDoHeaderAndContent = 3
    }


    override fun renderContent(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               rectanglesModel: RectanglesModel): Rectangle {
        return renderToDoDescription(g, rect, editorImpl, rectanglesModel)
    }

    private fun renderToDoDescription(g: Graphics,
                                      rect: Rectangle,
                                      editorImpl: EditorImpl,
                                      rectanglesModel: RectanglesModel): Rectangle {
        return ContentSegmentsUtil.renderSegments(todo.description.content, g, rect, editorImpl, rectanglesModel)
    }

    override fun calculateContentHeight(editorImpl: EditorImpl): Int {
        return ContentSegmentsUtil.calculateContentHeight(todo.description.content, editorImpl)
    }

    override fun calculateContentWidth(editorImpl: EditorImpl): Int {
        return ContentSegmentsUtil.calculateContentWidth(todo.description.content, editorImpl)
    }

    override fun acceptContent(context: RectangleModelBuildContext) {
        val currentRect = Rectangle(context.rect)
        for (segment in todo.description.content) {
            val renderer = SegmentRenderer.getRendererFor(segment)
            val rect = Rectangle(currentRect).apply {
                width = renderer.calculateExpectedWidthInPixels(context.editorImpl)
                height = renderer.calculateExpectedHeightInPixels(context.editorImpl)
            }

            context.rectanglesModel.addElement(segment, rect)
            renderer.accept(context.withRectangle(rect))
            currentRect.y += rect.height + ContentSegmentsUtil.deltaBetweenSegments
        }
    }
}