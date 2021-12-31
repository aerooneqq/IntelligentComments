package com.intelligentComments.core.comments.listeners

import com.intelligentComments.core.comments.RiderCommentsController
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import java.awt.Cursor

class CursorMouseMoveListener(project: Project) : EditorMouseMotionListener {
  private val commentsController = project.service<RiderCommentsController>()

  override fun mouseMoved(e: EditorMouseEvent) {
    val allFoldings = commentsController.getAllFoldingsFor(e.editor)

    val point = e.mouseEvent.point
    for (folding in allFoldings) {
      val bounds = folding.getBounds()
      if (bounds != null && bounds.contains(point)) {
        UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        return
      }
    }

    UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR))
  }
}