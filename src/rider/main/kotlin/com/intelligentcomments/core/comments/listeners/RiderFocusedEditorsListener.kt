package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.CommentsGutterMarksManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.project.Project
import com.jetbrains.rdclient.editors.FrontendTextControlHost

class RiderFocusedEditorsListener(private val project: Project) : FocusChangeListener {
  private val textControlHost = FrontendTextControlHost.getInstance(project)

  var lastFocusedEditor: Editor? = null
    private set


  override fun focusGained(editor: Editor) {
    lastFocusedEditor = editor
    for ((_, openedEditor) in textControlHost.openedEditors) {
      if (openedEditor != editor) {
        project.service<CommentsGutterMarksManager>().ensureAllGuttersAreInvisible(openedEditor)
      }
    }
  }
}