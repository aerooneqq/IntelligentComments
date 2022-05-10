package com.intelligentcomments.core.comments.listeners

import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

class CommentsEditorsListenersManager(private val project: Project) {
  private val processedEditors = mutableSetOf<Editor>()
  private val processedRegions = mutableSetOf<CustomFoldRegion>()


  fun attachListenersToEditorIfNeeded(editor: Editor) {
    if (!processedEditors.contains(editor)) {
      editor.addEditorMouseMotionListener(CursorMouseMoveListener())
      editor.addEditorMouseMotionListener(GutterMarkVisibilityMouseMoveListener(project))
      processedEditors.add(editor)
    }
  }

  fun attachListenersIfNeeded(foldRegion: CustomFoldRegion) {
    if (processedRegions.contains(foldRegion)) return

    foldRegion.editor.addEditorMouseListener(CommentMouseListener(foldRegion))
    foldRegion.editor.addEditorMouseMotionListener(CommentMouseMoveListener(foldRegion))

    attachListenersToEditorIfNeeded(foldRegion.editor)

    processedRegions.add(foldRegion)
  }
}