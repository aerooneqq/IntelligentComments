package com.intelligentcomments.core.comments.listeners

import com.intellij.openapi.editor.event.EditorMouseEvent

object CommentsListenersUtil {
  fun canProcessEvent(e: EditorMouseEvent) = e.mouseEvent.component == e.editor.contentComponent
}