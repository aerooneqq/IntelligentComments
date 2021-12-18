package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.IntelligentComment
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.DocCommentRenderer
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.document.getDocumentId
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.document.RiderDocumentHost
import java.util.*
import kotlin.collections.HashMap


class RiderCommentsController(project: Project) : LifetimedProjectComponent(project) {
  private val comments = HashMap<Document, TreeMap<CommentIdentifier, CommentBase>>()
  private val foldings = ViewableMap<CommentIdentifier, ViewableMap<Editor, CustomFoldRegion>>()
  private val logger = getLogger<RiderDocumentHost>()
  private val commentsStateManager = project.getService(RiderCommentsStateManager::class.java)
  private val textControlHost = FrontendTextControlHost.getInstance(project)


  fun addComment(editor: EditorImpl, comment: CommentBase) {
    application.assertIsDispatchThread()

    val document = editor.document
    val documentComments = if (document !in comments) {
      val map = TreeMap<CommentIdentifier, CommentBase>()
      comments[document] = map
      map
    } else {
      comments[document] ?: return
    }

    documentComments[comment.commentIdentifier] = comment

    val state = commentsStateManager.restoreOrCreateCommentState(editor, comment.commentIdentifier)
    updateRenderModeToMatchState(comment.commentIdentifier, editor.document, state)
  }

  fun toggleModeChange(commentIdentifier: CommentIdentifier, editor: EditorImpl) {
    application.assertIsDispatchThread()

    val comment = getComment(commentIdentifier, editor.document) ?: return
    val commentState = commentsStateManager.getExistingCommentState(editor, comment.commentIdentifier)
    if (commentState == null) {
      logger.logAssertion("Trying to change render mode for a not registered comment ${comment.commentIdentifier}")
      return
    }

    val changedState = commentsStateManager.changeRenderMode(editor, comment.commentIdentifier)
    if (changedState != null) {
      updateRenderModeToMatchState(commentIdentifier, editor.document, changedState)
    }
  }

  private fun updateRenderModeToMatchState(commentIdentifier: CommentIdentifier, document: Document, state: CommentState) {
    val documentId = document.getDocumentId(project) ?: return
    for (editor in textControlHost.getAllEditors(documentId)) {
      editor as? EditorImpl ?: continue
      if (!state.isInRenderMode) {
        toggleEditMode(commentIdentifier, editor)
      } else {
        toggleRenderMode(commentIdentifier, editor)
      }
    }
  }

  private fun toggleEditMode(commentIdentifier: CommentIdentifier, editor: EditorImpl) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentIdentifier, editor.document)
    if (correspondingComment != null) {
      val foldingModel = editor.foldingModel
      val folding = foldings[commentIdentifier]?.get(editor)
      if (folding != null) {
        editor.caretModel.moveToOffset(folding.startOffset)
        foldingModel.runBatchFoldingOperation {
          foldingModel.removeFoldRegion(folding)
          foldings[commentIdentifier]?.remove(editor)
        }
      }
    }
  }

  private fun getComment(commentIdentifier: CommentIdentifier, document: Document): CommentBase? {
    val documentComments = comments[document]
    val comment = documentComments?.get(commentIdentifier)

    if (comment == null){
      logger.error("Comment for given ID $commentIdentifier does not exist")
    }

    return comment
  }

  private fun toggleRenderMode(commentId: CommentIdentifier, editor: EditorImpl) {
    application.assertIsDispatchThread()

    val correspondingComment = getComment(commentId, editor.document)

    if (correspondingComment != null) {
      renderComment(correspondingComment, editor)
    }
  }

  private fun renderComment(comment: CommentBase, editor: EditorImpl) {
    val foldingModel = editor.foldingModel
    val startOffset = comment.rangeMarker.startOffset
    val endOffset = comment.rangeMarker.endOffset
    val document = editor.document

    val foldStartLine = document.getLineNumber(startOffset)
    val foldEndLine = document.getLineNumber(endOffset)
    val foldStartOffset = document.getLineStartOffset(foldStartLine)
    val foldEndOffset = document.getLineEndOffset(foldEndLine)

    foldingModel.runBatchFoldingOperation {
      val oldFolding = foldingModel.getFoldRegion(foldStartOffset, foldEndOffset)
      if (oldFolding != null && oldFolding is CustomFoldRegion && oldFolding.renderer is DocCommentRenderer) {
        val oldFoldingRenderer = oldFolding.renderer
        if (oldFoldingRenderer is DocCommentRenderer) {
          val oldComment = oldFoldingRenderer.model.docComment
          foldings[oldComment.commentIdentifier]?.remove(editor)
          foldingModel.removeFoldRegion(oldFolding)
        }
      }
    }

    foldingModel.runBatchFoldingOperation {
      val renderer = getCommentFoldingRenderer(comment, editor)
      val folding = foldingModel.addCustomLinesFolding(foldStartLine, foldEndLine, renderer)

      if (folding == null) {
        logger.error("Failed to create folding region for ${comment.id}")
      } else {
        val editorsFoldings = foldings.getOrCreate(comment.commentIdentifier) { ViewableMap() }
        editorsFoldings[editor] = folding
      }
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