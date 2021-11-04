package com.intelligentComments.ui.listeners

import com.intelligentComments.ui.comments.renderers.IntelligentCommentsRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener

class CommentMouseMoveListener(private val inlay: Inlay<*>) : EditorMouseMotionListener {
    private val renderer = inlay.renderer as IntelligentCommentsRenderer


    override fun mouseMoved(e: EditorMouseEvent) {
        val rectanglesModel = renderer.rectanglesModel ?: return
        val bounds = inlay.bounds ?: return
        if (rectanglesModel.dispatchMouseMove(e, bounds)) {
            inlay.repaint()
        }
    }
}