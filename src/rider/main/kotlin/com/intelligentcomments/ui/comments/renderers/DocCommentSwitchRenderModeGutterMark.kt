package com.intelligentcomments.ui.comments.renderers

import com.intelligentcomments.core.comments.CommentsGutterMarksManager
import com.intelligentcomments.core.comments.RiderCommentsController
import com.intelligentcomments.core.comments.states.RiderCommentsStateManager
import com.intelligentcomments.core.domain.core.CommentBase
import com.intelligentcomments.core.settings.CommentsDisplayKind
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.ui.LayeredIcon
import com.intellij.util.application
import com.jetbrains.rdclient.daemon.highlighters.gutterMarks.MergableGutterIconRenderer
import com.jetbrains.rider.util.idea.Editor
import javax.swing.Icon

class DocCommentSwitchRenderModeGutterMark(
  val comment: CommentBase,
  val editor: Editor,
  project: Project
) : GutterIconRenderer(), MergableGutterIconRenderer {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val commentsStatesManager = project.getComponent(RiderCommentsStateManager::class.java)
  private val guttersManager = project.service<CommentsGutterMarksManager>()

  var isVisible: Boolean = guttersManager.getGutterVisibilityFor(editor, comment)


  override fun equals(other: Any?): Boolean {
    return other is DocCommentSwitchRenderModeGutterMark && comment == other.comment && editor == other.editor
  }

  override fun hashCode(): Int {
    return comment.hashCode()
  }

  override fun getIcon(): Icon {
    val icon = when(commentsStatesManager.isInRenderMode(editor, comment.identifier)) {
      true -> AllIcons.Gutter.JavadocEdit
      false -> AllIcons.Gutter.JavadocRead
      else -> AllIcons.Gutter.Unique
    }

    return LayeredIcon(icon).apply {
      setLayerEnabled(0, isVisible)
    }
  }

  override fun getAlignment(): Alignment = Alignment.LEFT
  override fun getWeight(): Int {
    return Int.MAX_VALUE
  }

  override fun getClickAction(): AnAction {
    return object : AnAction() {
      override fun actionPerformed(e: AnActionEvent) {
        e.dataContext.Editor?.let { editor ->
          val commentStartLogicalPos = editor.offsetToLogicalPosition(comment.identifier.rangeMarker.startOffset)
          editor.caretModel.moveToLogicalPosition(commentStartLogicalPos)

          controller.toggleModeChange(comment.identifier, editor as EditorImpl) {
            if (it == CommentsDisplayKind.Hide || it == CommentsDisplayKind.Render) {
              CommentsDisplayKind.Code
            } else {
              RiderIntelligentCommentsSettingsProvider.getInstance().commentsDisplayKind.value
            }
          }
        }

        application.invokeLater {
          guttersManager.makeGutterVisibleImmediately(comment, editor)
        }
      }
    }
  }
}