package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.CommentsGutterMarksManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.editors.getEditor

class FocusedEditorsListener(project: Project) {
  init {
    val host = FrontendTextControlHost.getInstance(project)
    val gutterManager = project.service<CommentsGutterMarksManager>()
    host.adviseFocusedTextControlChange(project.lifetime) {
      val editor = it?.getEditor(project) ?: return@adviseFocusedTextControlChange
      for ((_, openedEditor) in host.openedEditors) {
        if (openedEditor != editor) {
          gutterManager.ensureAllGuttersAreInvisible(openedEditor)
        }
      }
    }
  }
}