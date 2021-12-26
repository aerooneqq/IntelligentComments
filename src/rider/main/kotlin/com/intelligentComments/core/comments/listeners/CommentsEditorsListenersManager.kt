package com.intelligentComments.core.comments.listeners

import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.project.Project

class CommentsEditorsListenersManager(private val project: Project) {
  private val processedRegions = mutableSetOf<CustomFoldRegion>()

  fun attachListenersIfNeeded(foldRegion: CustomFoldRegion) {
    if (processedRegions.contains(foldRegion)) return

    foldRegion.editor.addEditorMouseListener(CommentMouseListener(foldRegion))
    foldRegion.editor.addEditorMouseMotionListener(CommentMouseMoveListener(foldRegion))
    processedRegions.add(foldRegion)
  }
}