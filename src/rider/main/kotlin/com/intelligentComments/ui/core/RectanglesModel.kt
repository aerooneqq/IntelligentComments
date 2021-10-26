package com.intelligentComments.ui.core

import com.intelligentComments.ui.CommentsUtil
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.util.getOrCreate
import java.awt.Point
import java.awt.Rectangle

class RectanglesModelHolder(private val uiModel: IntelligentCommentUiModel) {
    var model: RectanglesModel? = null
        set(value) {
            application.assertIsDispatchThread()
            field = value
        }


    fun revalidate(editor: EditorImpl, xDelta: Int, yDelta: Int): RectanglesModel {
        application.assertIsDispatchThread()
        val newModel = CommentsUtil.buildRectanglesModel(editor, uiModel, xDelta, yDelta)
        model = newModel
        return newModel
    }
}

class RectanglesModel {
    private val rectanglesToElements = HashMap<Rectangle, MutableList<UiInteractionModelBase>>()
    private val elementsToRectangles = HashMap<UiInteractionModelBase, MutableList<Rectangle>>()
    private var sealed = false

    private var myWidth: Int = -1
    private var myHeight: Int = -1

    val width
        get() = myWidth
    val height
        get() = myHeight
    val allRectangles: Iterable<Rectangle>
        get() = rectanglesToElements.keys


    fun addElement(model: UiInteractionModelBase, rect: Rectangle) {
        application.assertIsDispatchThread()
        checkCanChange()

        val models = rectanglesToElements.getOrCreate(rect) { mutableListOf() }
        models.add(model)

        val rectangles = elementsToRectangles.getOrCreate(model) { mutableListOf() }
        rectangles.add(rect)
    }

    fun setSize(width: Int, height: Int) {
        checkCanChange()
        myWidth = width
        myHeight = height
    }

    private fun checkCanChange() {
        if (sealed) throw IllegalStateException("Model is already sealed, can't change it")
    }

    fun seal() {
        application.assertIsDispatchThread()
        if (sealed) return
        if (width == -1 || height == -1) throw IllegalStateException("Can't seal when width or height is not set")

        sealed = true
    }

    fun dispatchMouseMove(e: EditorMouseEvent): Boolean {
        application.assertIsDispatchThread()
        val point = e.mouseEvent.point
        var anyUiChange = false

        executeWithRectangleAndModels { rect, model ->
            if (!rect.contains(point) && model.mouseIn) {
                if (model.handleMouseOut(e)) {
                    anyUiChange = true
                }
            }
        }

        executeWithRectangleAndModels { rect, model ->
            if (rect.contains(point) && !model.mouseIn) {
                if (model.handleMouseIn(e)) {
                    anyUiChange = true
                }
            }
        }

        return anyUiChange
    }

    private fun executeWithRectangleAndModels(action: (Rectangle, UiInteractionModelBase) -> Unit) {
        for ((rect, models) in rectanglesToElements) {
            for (model in models) {
                action(rect, model)
            }
        }
    }

    fun dispatchMouseClick(event: EditorMouseEvent): Boolean {
        application.assertIsDispatchThread()
        var anyUiChange = false

        executeWithRectangleAndModels { rect, model ->
            if (rect.contains(event.mouseEvent.point)) {
                if (model.handleClick(event)) {
                    anyUiChange = true
                }
            }
        }

        return anyUiChange
    }
}