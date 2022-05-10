package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import java.awt.Rectangle

class CommentMouseListener(private val foldRegion: CustomFoldRegion) : EditorMouseListener {
  private val renderer = foldRegion.renderer as RendererWithRectangleModel


  override fun mouseClicked(event: EditorMouseEvent) {
    if (!CommentsListenersUtil.canProcessEvent(event)) return

    val rectanglesModel = renderer.rectanglesModel ?: return
    val bounds = foldRegion.getBounds() ?: return

    if (rectanglesModel.dispatchMouseClick(event, bounds)) {
      foldRegion.update()
      foldRegion.repaint()
      event.editor.component.revalidate()
      event.editor.component.repaint()
    }
  }
}

fun CustomFoldRegion.getBounds(): Rectangle? {
  val location = this.location ?: return null

  return Rectangle(location.x, location.y, widthInPixels, heightInPixels)
}