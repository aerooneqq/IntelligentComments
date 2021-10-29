package com.intelligentComments.ui.comments.renderers.todos

import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.model.todo.ToDoUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.RectanglesModelUtil
import com.intelligentComments.ui.util.RectanglesModelUtil.Companion.deltaBetweenHeaderAndContent
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

interface ToDosRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(section: SectionWithHeaderUiModel<ToDoUiModel>): ToDosRenderer {
            return ToDosRendererImpl(section)
        }
    }
}

class ToDosRendererImpl(private val section: SectionWithHeaderUiModel<ToDoUiModel>) : VerticalSectionWithHeaderRenderer<ToDoUiModel>(section), ToDosRenderer {
    companion object {
        const val deltaBetweenToDos = 10
    }


    override fun renderContent(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               rectanglesModel: RectanglesModel): Rectangle {
        var adjustedRect = Rectangle(rect)

        executeActionWithToDosAndRenderers { todo, renderer ->
            adjustedRect = renderer.render(g, adjustedRect, editorImpl, rectanglesModel)
            adjustedRect.y += deltaBetweenToDos
        }

        return adjustedRect.apply {
            y -= deltaBetweenToDos
        }
    }

    private fun executeActionWithToDosAndRenderers(action: (ToDoUiModel, ToDoRenderer) -> Unit) {
        for (todo in section.content) {
            val renderer = ToDoRenderer.getRendererFor(todo)
            action(todo, renderer)
        }
    }

    override fun calculateContentWidth(editorImpl: EditorImpl): Int {
        var width = 0
        executeActionWithToDosAndRenderers { _, renderer ->
            width = max(width, renderer.calculateExpectedWidthInPixels(editorImpl))
        }

        return width
    }

    override fun calculateContentHeight(editorImpl: EditorImpl): Int {
        var height = 0
        executeActionWithToDosAndRenderers { _, renderer ->
            height += renderer.calculateExpectedHeightInPixels(editorImpl)
            height += deltaBetweenToDos
        }

        return height - deltaBetweenToDos
    }

    override fun acceptContent(context: RectangleModelBuildContext) {
        RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, deltaBetweenHeaderAndContent)

        executeActionWithToDosAndRenderers { todo, renderer ->
            renderer.accept(context)
            RectanglesModelUtil.updateHeightAndWidthAndAddModel(renderer, context, todo)
            RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, deltaBetweenToDos)
        }

        RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, -deltaBetweenToDos)
    }
}