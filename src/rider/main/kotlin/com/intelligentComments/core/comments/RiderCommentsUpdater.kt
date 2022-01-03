package com.intelligentComments.core.comments

import com.intelligentComments.core.changes.Change
import com.intelligentComments.core.changes.ChangeListener
import com.intelligentComments.core.changes.ChangeManager
import com.intelligentComments.core.changes.RenderAffectedCommentChange
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent

class RiderCommentsUpdater(project: Project) : LifetimedProjectComponent(project), ChangeListener {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val textControlHost = FrontendTextControlHost.getInstance(project)


  init {
    project.service<ChangeManager>().addListener(componentLifetime, this)
  }


  override fun handleChange(change: Change) {
    if (change is RenderAffectedCommentChange) {
      for ((_, editor) in textControlHost.openedEditors) {
        controller.getFolding(change.id, editor as EditorImpl)?.repaint()
      }
    }
  }
}