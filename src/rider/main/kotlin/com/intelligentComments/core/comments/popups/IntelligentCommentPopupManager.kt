package com.intelligentComments.core.comments.popups

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.listeners.getBounds
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
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
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

class IntelligentCommentPopupManager(project: Project) : LifetimedService() {
  private val popupLifetimes = SequentialLifetimes(serviceLifetime)
  private val commentsController = project.getComponent(RiderCommentsController::class.java)

  private var mouseInPopup = false
  private var currentlyOpenedPopup: IntelligentCommentPopup? = null


  val openedPopup: IntelligentCommentPopup?
    get() {
      application.assertIsDispatchThread()
      return currentlyOpenedPopup
    }


  fun showPopupFor(
    model: UiInteractionModelBase,
    editor: Editor,
    relativePoint: RelativePoint
  ) {
    val popupLifetime = popupLifetimes.next()
    val popup = IntelligentCommentPopup(model, editor)

    attachListenerDuringPopupAlive(popupLifetime, popup, editor, relativePoint)
    currentlyOpenedPopup = popup

    popup.show(relativePoint)
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

  fun tryHideActivePopup() {
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

  fun calculatePointForPopupInComment(
    comment: CommentBase,
    e: EditorMouseEvent
  ): RelativePoint {
    val folding = commentsController.getFolding(comment.identifier, e.editor)
    val textHeight = TextUtil.getTextHeight(e.editor as EditorImpl, null)
    val mousePoint = e.mouseEvent.point

    val bounds = folding?.getBounds()
    val adjustedPopupLeftUpPoint = if (bounds != null) {
      Point(bounds.x, bounds.y + textHeight + 5)
    } else {
      Point(mousePoint.x, mousePoint.y + textHeight)
    }

    return RelativePoint(e.mouseEvent.component, mousePoint.apply {
      x = adjustedPopupLeftUpPoint.x
      y = adjustedPopupLeftUpPoint.y
    })
  }
}