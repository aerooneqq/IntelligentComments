package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.domain.core.DocComment
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.jetbrains.rider.util.idea.Editor
import javax.swing.Icon

class DocCommentSwitchRenderModeGutterMark(
  private val docComment: DocComment,
  private val editorImpl: EditorImpl,
  project: Project
) : GutterIconRenderer() {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val commentsStatesManager = project.service<RiderCommentsStateManager>()


  override fun equals(other: Any?): Boolean {
    return other is DocCommentSwitchRenderModeGutterMark && docComment == other.docComment
  }

  override fun hashCode(): Int {
    return docComment.hashCode()
  }

  override fun getIcon(): Icon {
    return when(commentsStatesManager.isInRenderMode(editorImpl, docComment.commentIdentifier)) {
      true -> AllIcons.Gutter.JavadocEdit
      false -> AllIcons.Gutter.JavadocRead
      else -> AllIcons.Gutter.Unique
    }
  }

  override fun getAlignment(): Alignment = Alignment.LEFT
  override fun getTooltipText(): String =
    when (commentsStatesManager.isInRenderMode(editorImpl, docComment.commentIdentifier)) {
      true -> "Go to edit mode"
      false -> "Go to render mode"
      else -> ""
    }

  override fun getClickAction(): AnAction = object : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
      e.dataContext.Editor?.let { editor ->
        controller.toggleModeChange(docComment.commentIdentifier, editor as EditorImpl)
      }
    }
  }
}