package com.intelligentcomments.core.comments

import com.intelligentcomments.core.changes.*
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.util.reactive.AddRemove
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent

class RiderCommentsUpdater(project: Project) : LifetimedProjectComponent(project), ChangeListener {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val gutterMarksManager = project.service<CommentsGutterMarksManager>()
  private val textControlHost = FrontendTextControlHost.getInstance(project)
  private val openedEditorsStamps = HashMap<Editor, Long>()

  private var globalChangesStamp: Long = 0


  init {
    ChangeManager.getInstance().addListener(componentLifetime, this)

    textControlHost.openedEditors.adviseAddRemove(componentLifetime) { addRemove, _, editor ->
      if (addRemove == AddRemove.Remove) {
        openedEditorsStamps.remove(editor)
      } else if (addRemove == AddRemove.Add) {
        openedEditorsStamps[editor] = globalChangesStamp
      }
    }

    textControlHost.visibleEditorsChange.advise(componentLifetime) {
      for (editor in it.newVisible) {
        val lastHandledGlobalChangeInEditor = openedEditorsStamps[editor] ?: continue
        if (lastHandledGlobalChangeInEditor != globalChangesStamp) {
          openedEditorsStamps[editor] = globalChangesStamp
          application.invokeLater {
            controller.reRenderAllComments(editor)
            gutterMarksManager.ensureAllGuttersAreInvisible(editor)
          }
        }
      }
    }
  }


  override fun handleChange(change: Change) {
    application.assertIsDispatchThread()

    if (change is RenderAffectedCommentChange) {
      for (editor in textControlHost.visibleEditorsChange.value.newVisible) {
        val folding = controller.getFolding(change.id, editor)
        updateFolding(editor, folding)
      }
    }

    if (change is ThemeChange || change is SettingsChange) {
      val newGlobalStamp = globalChangesStamp + 1
      for (editor in textControlHost.visibleEditorsChange.value.newVisible) {
        openedEditorsStamps[editor] = newGlobalStamp
        application.invokeLater {
          controller.reRenderAllComments(editor)
        }
      }

      globalChangesStamp = newGlobalStamp
    }
  }

  private fun updateFolding(editor: Editor, folding: CustomFoldRegion?) {
    val renderer = folding?.renderer as? RendererWithRectangleModel ?: return
    renderer.invalidateRectangleModel(editor as EditorImpl)
    folding.update()
    folding.repaint()
  }
}