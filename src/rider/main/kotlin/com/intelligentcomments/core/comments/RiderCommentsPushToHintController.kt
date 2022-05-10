package com.intelligentcomments.core.comments

import com.intelligentcomments.core.domain.core.CommentBase
import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.and
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import java.awt.AWTEvent
import java.awt.event.KeyEvent

class RiderCommentsPushToHintController(project: Project) : LifetimedProjectComponent(project) {
  init {
    val ctrlPressed = Property(false)
    val cPressed = Property(false)
    val bothKeysPressed = ctrlPressed.and(cPressed)
    val controller = project.getComponent(RiderCommentsController::class.java)
    val textControlHost = FrontendTextControlHost.getInstance(project)

    val lifetimes = SequentialLifetimes(componentLifetime)
    bothKeysPressed.change.advise(componentLifetime) { pressed ->
      if (!pressed) {
        lifetimes.terminateCurrent()
        return@advise
      }

      val lifetime = lifetimes.next()
      val lastFocusedTextControlId = textControlHost.lastFocusedTextControl ?: return@advise
      val currentEditor = textControlHost.tryGetEditor(lastFocusedTextControlId) as? EditorImpl ?: return@advise
      val comment = findNearestComment(currentEditor) ?: return@advise
      controller.displayInRenderMode(comment, currentEditor, lifetime)
    }

    val handler = fun (e: AWTEvent): Boolean {
      if (e is KeyEvent) {
        if (e.keyCode == KeyEvent.VK_CONTROL) {
          ctrlPressed.set(e.isControlDown)
        }

        if (e.keyCode == KeyEvent.VK_C) {
          cPressed.set(e.id == KeyEvent.KEY_PRESSED)
        }
      }

      return false
    }

    IdeEventQueue.getInstance().addPostprocessor(handler, componentLifetime.createNestedDisposable())
  }
}

fun findNearestComment(editorImpl: EditorImpl): CommentBase? {
  val project = editorImpl.project ?: return null

  val controller = project.getComponent(RiderCommentsController::class.java)
  return controller.findNearestLeftCommentToCurrentOffset(editorImpl)
}