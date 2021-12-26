package com.intelligentComments.core.comments.listeners

import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener

class CommentMouseMoveListener(private val foldRegion: CustomFoldRegion) : EditorMouseMotionListener {
  private val renderer = foldRegion.renderer as RendererWithRectangleModel


  override fun mouseMoved(e: EditorMouseEvent) {
    val rectanglesModel = renderer.rectanglesModel ?: return
    val bounds = foldRegion.getBounds() ?: return
    if (rectanglesModel.dispatchMouseMove(e, bounds)) {
      foldRegion.repaint()
    }
  }
}