package com.intelligentComments.ui.listeners

import com.intelligentComments.ui.comments.renderers.IntelligentCommentsRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener

class CommentMouseListener(private val inlay: Inlay<*>) : EditorMouseListener {
    private val renderer = inlay.renderer as IntelligentCommentsRenderer


    override fun mouseClicked(event: EditorMouseEvent) {
        val rectanglesModel = renderer.rectanglesModel ?: return

        if (rectanglesModel.dispatchMouseClick(event.mouseEvent.point)) {
            inlay.update()
            inlay.repaint()
            event.editor.component.revalidate()
            event.editor.component.repaint()
        }
    }
}