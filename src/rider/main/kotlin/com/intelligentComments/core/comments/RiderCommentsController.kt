package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.IntelligentComment
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent


class RiderCommentsController(project: Project) : LifetimedProjectComponent(project) {
  private val comments = ViewableMap<Document, ViewableMap<Int, CommentBase>>()


  fun addComment(editor: EditorImpl, comment: CommentBase, lifetime: Lifetime) {
    application.assertIsDispatchThread()

    val document = editor.document
    val documentComments = if (document !in comments) {
      val map = ViewableMap<Int, CommentBase>()
      comments[document] = map
      map
    } else {
      comments[document] ?: return
    }

    documentComments[comment.commentIdentifier] = comment
    updateComment(editor, comment.commentIdentifier, lifetime)
  }


  fun updateComment(editor: EditorImpl, commentIdentifier: Int, lifetime: Lifetime) {
    application.assertIsDispatchThread()

    val document = editor.document
    val documentComments = comments[document]
    val correspondingComment = documentComments?.get(commentIdentifier)

    if (correspondingComment != null) {
      renderComment(correspondingComment, editor)
      return
    }

    if (documentComments != null) {
      subscribeToCommentAppearance(lifetime, editor, commentIdentifier, documentComments)
      return
    }

    comments.advise(lifetime) {
      if (it.key == document) {
        subscribeToCommentAppearance(lifetime, editor, commentIdentifier, it.newValueOpt ?: return@advise)
      }
    }
  }

  private fun subscribeToCommentAppearance(
    lifetime: Lifetime,
    editor: EditorImpl,
    commentIdentifier: Int,
    map: ViewableMap<Int, CommentBase>
  ) {
    map.advise(lifetime) {
      if (it.key == commentIdentifier) {
        val comment = it.newValueOpt ?: return@advise
        renderComment(comment, editor)
      }
    }
  }

  private fun renderComment(comment: CommentBase, editor: EditorImpl) {
    val foldingModel = editor.foldingModel
    val startOffset = comment.highlighter.startOffset
    val endOffset = comment.highlighter.endOffset
    val document = editor.document

    foldingModel.runBatchFoldingOperation {
      val foldStartLine = document.getLineNumber(startOffset)
      val foldEndLine = document.getLineNumber(endOffset)

      foldingModel.addCustomLinesFolding(foldStartLine, foldEndLine, getCommentFoldingRenderer(comment, editor))
    }
  }

  private fun getCommentFoldingRenderer(comment: CommentBase, editor: Editor): CustomFoldRegionRenderer {
    return when (comment) {
      is DocComment -> DocCommentUiModel(comment, project, editor).getCustomFoldRegionRenderer(project)
      is IntelligentComment -> IntelligentCommentUiModel(project, comment).getCustomFoldRegionRenderer(project)
      else -> throw IllegalArgumentException(comment.javaClass.name)
    }
  }
}