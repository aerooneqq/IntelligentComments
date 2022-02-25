package com.intelligentComments.core.comments.docs

import com.intelligentComments.core.comments.createCommentUiModel
import com.intelligentComments.core.comments.popups.IntelligentCommentPopupManager
import com.intelligentComments.core.domain.core.CommentBase
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.platform.util.idea.LifetimedService

class CommentsHoverDocManager(private val project: Project) : LifetimedService() {
  private val popupManager = project.service<IntelligentCommentPopupManager>()


  fun showHoverDoc(
    comment: CommentBase,
    e: EditorMouseEvent
  ) {
    application.assertIsDispatchThread()
    if (popupManager.openedPopup != null) {
      return
    }

    val editor = e.editor
    val model = comment.createCommentUiModel(project, editor)
    val point = popupManager.calculatePointForPopupInComment(comment, e)
    popupManager.showPopupFor(model, editor, point)
  }

  fun tryHideHoverDoc() {
    popupManager.tryHideActivePopup()
  }
}