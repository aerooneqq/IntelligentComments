package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.CommentsGutterMarksManager
import com.intelligentcomments.hacks.FrontendTextControlHostHacks
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.project.Project
import com.intellij.util.application

class RiderFocusedEditorsListener(private val project: Project) : FocusChangeListener {
  private val textControlHost = project.getComponent(FrontendTextControlHostHacks::class.java)


  var lastFocusedEditor: Editor? = null
    private set


  override fun focusGained(editor: Editor) {
    application.assertIsDispatchThread()

    lastFocusedEditor = editor
    for ((_, openedEditor) in textControlHost.getOpenedEditors()) {
      if (openedEditor != editor) {
        project.service<CommentsGutterMarksManager>().ensureAllGuttersAreInvisible(openedEditor)
      }
    }
  }
}