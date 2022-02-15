package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.jetbrains.rdclient.daemon.highlighters.gutterMarks.MergableGutterIconRenderer
import com.jetbrains.rider.util.idea.Editor
import javax.swing.Icon

class DocCommentSwitchRenderModeGutterMark(
  private val comment: CommentBase,
  private val editor: Editor,
  project: Project
) : GutterIconRenderer(), MergableGutterIconRenderer {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val commentsStatesManager = project.getComponent(RiderCommentsStateManager::class.java)


  override fun equals(other: Any?): Boolean {
    return other is DocCommentSwitchRenderModeGutterMark && comment == other.comment
  }

  override fun hashCode(): Int {
    return comment.hashCode()
  }

  override fun getIcon(): Icon {
    return when(commentsStatesManager.isInRenderMode(editor, comment.commentIdentifier)) {
      true -> AllIcons.Gutter.JavadocEdit
      false -> AllIcons.Gutter.JavadocRead
      else -> AllIcons.Gutter.Unique
    }
  }

  override fun getAlignment(): Alignment = Alignment.LEFT
  override fun getTooltipText(): String =
    when (commentsStatesManager.isInRenderMode(editor, comment.commentIdentifier)) {
      true -> "Go to edit mode"
      false -> "Go to render mode"
      else -> ""
    }

  override fun getWeight(): Int {
    return Int.MAX_VALUE
  }

  override fun getClickAction(): AnAction = object : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
      e.dataContext.Editor?.let { editor ->
        controller.toggleModeChange(comment.commentIdentifier, editor as EditorImpl) {
          if (it == CommentsDisplayKind.Hide || it == CommentsDisplayKind.Render) {
            CommentsDisplayKind.Code
          } else {
            RiderIntelligentCommentsSettingsProvider.getInstance().commentsDisplayKind.value
          }
        }
      }
    }
  }
}