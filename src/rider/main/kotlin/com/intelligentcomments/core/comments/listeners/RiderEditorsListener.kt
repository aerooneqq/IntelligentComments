package com.intelligentcomments.core.comments.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener

class RiderEditorsListener : EditorFactoryListener {
  override fun editorCreated(event: EditorFactoryEvent) {
    event.editor.project?.service<CommentsEditorsListenersManager>()?.attachListenersToEditorIfNeeded(event.editor)
  }
}