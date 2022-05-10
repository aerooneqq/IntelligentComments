package com.intelligentcomments.core.actions

import com.intelligentcomments.core.comments.RiderCommentsController
import com.intelligentcomments.core.comments.states.canChangeFromCodeToRender
import com.intelligentcomments.core.settings.CommentsDisplayKind
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.jetbrains.rider.util.idea.Editor

class HideOrShowCommentsInEditorAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val editor = e.dataContext.Editor as? EditorImpl ?: return
    e.project?.let {
      val controller = it.getComponent(RiderCommentsController::class.java)
      if (!canChangeFromCodeToRender(editor)) {
        controller.collapseOrExpandAllFoldingsInCodeMode(editor)
        return@let
      }

      controller.changeStatesForAllCommentsInEditor(editor) { kind ->
        if (kind == CommentsDisplayKind.Render) {
          CommentsDisplayKind.Hide
        } else {
          CommentsDisplayKind.Render
        }
      }
    }
  }
}