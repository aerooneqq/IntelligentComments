package com.intelligentComments.core.comments.listeners

import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.util.Alarm

class CommentMouseMoveListener(private val foldRegion: CustomFoldRegion) : EditorMouseMotionListener {
  private val renderer = foldRegion.renderer as RendererWithRectangleModel
  private val longMousePresenceAlarm = Alarm()

  private var stamp = 0


  override fun mouseMoved(e: EditorMouseEvent) {
    ++stamp
    val rectanglesModel = renderer.rectanglesModel ?: return
    val bounds = foldRegion.getBounds() ?: return
    if (rectanglesModel.dispatchMouseMove(e, bounds)) {
      foldRegion.repaint()
    }

    val cachedStamp = stamp
    longMousePresenceAlarm.addRequest({
      if (stamp == cachedStamp) {
        if (rectanglesModel.dispatchLongMousePresence(e, bounds)) {
          foldRegion.repaint()
        }
      }
    }, 1000)
  }
}