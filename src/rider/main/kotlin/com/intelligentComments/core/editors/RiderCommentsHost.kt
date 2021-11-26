package com.intelligentComments.core.editors

import com.intelligentComments.core.domain.rd.DocCommentFromRd
import com.intelligentComments.core.domain.rd.IntelligentCommentFromRd
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.listeners.CommentMouseListener
import com.intelligentComments.ui.listeners.CommentMouseMoveListener
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.ex.FoldingListener
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.document.getDocumentId
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.editors.RiderTextControlHost
import com.jetbrains.rider.projectView.solution

class RiderCommentsHost(project: Project) : LifetimedProjectComponent(project) {
  private val comments = ViewableMap<RdDocumentId, ViewableMap<Int, RdComment>>()


  init {
    project.solution.rdCommentsModel.comments.advise(componentLifetime) {
      val rdDocumentComments = it.newValueOpt ?: return@advise
      val commentsMap = ViewableMap<Int, RdComment>()
      comments[it.key] = commentsMap

      for (comment in rdDocumentComments.comments) {
        commentsMap[comment.commentIdentifier] = comment
      }
    }
  }


  fun renderDocComment(editor: EditorImpl, commentIdentifier: Int, lifetime: Lifetime) {
    application.assertIsDispatchThread()

    val document = editor.document
    val project = editor.project ?: return
    val documentId = document.getDocumentId(project) ?: return

    val documentComments = comments[documentId]
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
      if (it.key == documentId) {
        subscribeToCommentAppearance(lifetime, editor, commentIdentifier, it.newValueOpt ?: return@advise)
      }
    }
  }

  private fun subscribeToCommentAppearance(lifetime: Lifetime,
                                           editor: EditorImpl,
                                           commentIdentifier: Int,
                                           map: ViewableMap<Int, RdComment>) {
    map.advise(lifetime) {
      if (it.key == commentIdentifier) {
        val comment = it.newValueOpt ?: return@advise
        renderComment(comment, editor)
      }
    }
  }

  private fun renderComment(comment: RdComment, editor: EditorImpl) {
    val inlayProperties = InlayProperties().apply {
      showAbove(true)
      showWhenFolded(true)
      relatesToPrecedingText(false)
    }

    val foldingModel = editor.foldingModel as FoldingModelImpl
    val startOffset = comment.range.startOffset
    val endOffset = comment.range.endOffset
    val document = editor.document

    foldingModel.runBatchFoldingOperation {
      val foldStartLine = document.getLineNumber(startOffset)
      val foldEndLine = document.getLineNumber(endOffset)
      val foldStartOffset = document.getLineStartOffset(foldStartLine)
      val foldEndOffset = if (foldEndLine < document.lineCount - 1) {
        document.getLineStartOffset(foldEndLine + 1)
      } else {
        document.getLineEndOffset(foldEndLine)
      }

      foldingModel.addCustomLinesFolding(foldStartLine, foldEndLine, getCommentFoldingRenderer(comment, editor))
    }
  }

  private fun getCommentFoldingRenderer(rdComment: RdComment, editor: Editor): CustomFoldRegionRenderer {
    return when(rdComment) {
      is RdDocComment -> DocCommentUiModel(DocCommentFromRd(rdComment, project), project, editor).getCustomFoldRegionRenderer(project)
      is RdIntelligentComment -> IntelligentCommentUiModel(project, IntelligentCommentFromRd(rdComment, project)).getCustomFoldRegionRenderer(project)
      else -> throw IllegalArgumentException(rdComment.javaClass.name)
    }
  }

  private fun getCommentRenderer(rdComment: RdComment, editor: Editor): EditorCustomElementRenderer {
    return when(rdComment) {
      is RdDocComment -> DocCommentUiModel(DocCommentFromRd(rdComment, project), project, editor).getEditorCustomElementRenderer(project)
      is RdIntelligentComment -> IntelligentCommentUiModel(project, IntelligentCommentFromRd(rdComment, project)).getEditorCustomElementRenderer(project)
      else -> throw IllegalArgumentException(rdComment.javaClass.name)
    }
  }
}