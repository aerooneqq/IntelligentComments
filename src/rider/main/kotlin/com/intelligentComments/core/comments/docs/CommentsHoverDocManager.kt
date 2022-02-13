package com.intelligentComments.core.comments.docs

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.createCommentUiModel
import com.intelligentComments.core.comments.listeners.getBounds
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.ui.popups.IntelligentCommentPopup
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.application
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rd.ui.addMouseMotionListener
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

class CommentsHoverDocManager(private val project: Project) : LifetimedService() {
  private val popupLifetimes = SequentialLifetimes(serviceLifetime)
  private val commentsController = project.getComponent(RiderCommentsController::class.java)

  private var currentlyOpenedPopup: IntelligentCommentPopup? = null
  private var mouseInPopup = false


  fun showHoverDoc(
    comment: CommentBase,
    e: EditorMouseEvent
  ) {
    application.assertIsDispatchThread()

    val editor = e.editor
    var popup = currentlyOpenedPopup
    if (popup != null) {
      return
    }

    val model = comment.createCommentUiModel(project, editor as EditorImpl)
    val popupLifetime = popupLifetimes.next()

    popup = IntelligentCommentPopup(model, editor)
    val point = calculatePointForHoverDoc(comment, e)
    attachListenerDuringPopupAlive(popupLifetime, popup, editor, point)
    currentlyOpenedPopup = popup

    popup.show(point)
  }

  private fun calculatePointForHoverDoc(
    comment: CommentBase,
    e: EditorMouseEvent
  ): RelativePoint {
    val folding = commentsController.getFolding(comment.commentIdentifier, e.editor as EditorImpl)
    val textHeight = TextUtil.getTextHeight(e.editor as EditorImpl, null)
    val mousePoint = e.mouseEvent.point

    val adjustedPopupY = if (folding != null) {
      val bounds = folding.getBounds()
      if (bounds != null) {
        bounds.y + textHeight + 5
      } else {
        mousePoint.y + textHeight
      }
    } else {
      mousePoint.y + textHeight
    }

    return RelativePoint(e.mouseEvent.component, mousePoint.apply {
      y = adjustedPopupY
    })
  }

  private fun attachListenerDuringPopupAlive(
    popupLifetime: Lifetime,
    popup: IntelligentCommentPopup,
    editor: Editor,
    point: RelativePoint
  ) {
    val delta = 10
    val x = point.originalPoint.x - delta
    val y = point.originalPoint.y - delta
    val width = popup.popupSize.width + delta
    val height = popup.popupSize.height + delta
    val popupArea = Rectangle(x, y, width, height)

    editor.contentComponent.addMouseMotionListener(popupLifetime, object : MouseMotionListener {
      override fun mouseDragged(e: MouseEvent?) {
      }

      override fun mouseMoved(e: MouseEvent?) {
        val mousePoint = e?.point ?: return
        val newMouseInPopup = popupArea.contains(mousePoint)
        if (mouseInPopup && !newMouseInPopup) {
          closeCurrentPopup()
          return
        }

        mouseInPopup = newMouseInPopup
      }
    })
  }

  fun tryHideHoverDoc() {
    application.assertIsDispatchThread()

    if (!mouseInPopup) {
      closeCurrentPopup()
    }
  }

  private fun closeCurrentPopup() {
    application.assertIsDispatchThread()
    mouseInPopup = false
    currentlyOpenedPopup?.cancel(null)
    popupLifetimes.terminateCurrent()
    currentlyOpenedPopup = null
  }

  fun hasActiveHoverDoc(): Boolean {
    application.assertIsDispatchThread()
    val popup = currentlyOpenedPopup
    return popup != null && popup.isVisible
  }
}