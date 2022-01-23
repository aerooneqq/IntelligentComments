package com.intelligentComments.core.comments

import com.intelligentComments.core.changes.*
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent

class RiderCommentsUpdater(project: Project) : LifetimedProjectComponent(project), ChangeListener {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val textControlHost = FrontendTextControlHost.getInstance(project)


  init {
    ChangeManager.getInstance().addListener(componentLifetime, this)
  }


  override fun handleChange(change: Change) {
    if (change is RenderAffectedCommentChange) {
      for ((_, editor) in textControlHost.openedEditors) {
        val folding = controller.getFolding(change.id, editor as EditorImpl)
        updateFolding(editor, folding)
      }
    }

    if (change is ThemeChange || change is SettingsChange) {
      for ((_, editor) in textControlHost.openedEditors) {
        controller.reRenderAllComments(editor as EditorImpl)
      }
    }
  }

  private fun updateFolding(editor: Editor, folding: CustomFoldRegion?) {
    val renderer = folding?.renderer as? RendererWithRectangleModel ?: return
    renderer.invalidateRectangleModel(editor as EditorImpl)
    folding.update()
    folding.repaint()
  }
}