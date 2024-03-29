package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.RiderOpenedEditorsAndDocuments
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.reactive.AddRemove

class CommentsEditorsListenersManager(private val project: Project) {
  private val processedEditors = mutableSetOf<Editor>()
  private val processedRegions = mutableSetOf<CustomFoldRegion>()


  init {
    val openedEditorsAndDocuments = project.getComponent(RiderOpenedEditorsAndDocuments::class.java)
    openedEditorsAndDocuments.openedEditors.adviseAddRemove(project.lifetime) { addRemove, _, editor ->
      if (addRemove == AddRemove.Remove) {
        processedEditors.remove(editor)
        val regionsToRemove = mutableSetOf<FoldRegion>()
        for (folding in processedRegions) {
          if (folding.editor == editor) {
            regionsToRemove.add(folding)
          }
        }

        for (region in regionsToRemove) {
          processedRegions.remove(region)
        }
      }
    }
  }


  fun attachListenersToEditorIfNeeded(editor: Editor) {
    if (!processedEditors.contains(editor)) {
      editor.addEditorMouseMotionListener(CursorMouseMoveListener())
      editor.addEditorMouseMotionListener(GutterMarkVisibilityMouseMoveListener(project))
      (editor as? EditorEx)?.addFocusListener(project.getComponent(RiderFocusedEditorsListener::class.java))
      val model = editor.foldingModel as FoldingModelImpl
      model.addListener(FoldingExpansionsListener(), project.lifetime.createNestedDisposable())

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