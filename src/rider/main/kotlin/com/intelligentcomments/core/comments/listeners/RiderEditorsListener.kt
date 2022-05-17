package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.EditModeFolding
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener

class RiderEditorsListener : EditorFactoryListener {
  override fun editorCreated(event: EditorFactoryEvent) {
    event.editor.project?.service<CommentsEditorsListenersManager>()?.attachListenersToEditorIfNeeded(event.editor)
  }
}

fun isIntelligentCommentFolding(foldRegion: FoldRegion): Boolean {
  return foldRegion is CustomFoldRegion && foldRegion.renderer is RendererWithRectangleModel ||
         foldRegion.getUserData(EditModeFolding) != null
}