package com.intelligentComments.core.comments.listeners

import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.util.ui.UIUtil
import java.awt.Cursor

class CursorMouseMoveListener : EditorMouseMotionListener {
  override fun mouseMoved(e: EditorMouseEvent) {
    val foldings = (e.editor.foldingModel as FoldingModelImpl).getRegionsOverlappingWith(e.offset, e.offset)
    val mouseOverComment = foldings.any { it is CustomFoldRegion && it.renderer is RendererWithRectangleModel }
    val cursor = if (mouseOverComment) Cursor.HAND_CURSOR else Cursor.TEXT_CURSOR

    UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(cursor))
  }
}