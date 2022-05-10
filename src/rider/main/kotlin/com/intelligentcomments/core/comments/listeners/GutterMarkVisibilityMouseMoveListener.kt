package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.CommentsGutterMarksManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.project.Project

class GutterMarkVisibilityMouseMoveListener(project: Project) : EditorMouseMotionListener {
  private val guttersManager = project.service<CommentsGutterMarksManager>()

  override fun mouseMoved(e: EditorMouseEvent) {
    guttersManager.queueUpdate(e.editor, e.offset)
  }
}