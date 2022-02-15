package com.intelligentComments.core.markup

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.renderers.DocCommentSwitchRenderModeGutterMark
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range
import com.intellij.util.application
import com.jetbrains.rd.ide.model.RdCommentFoldingModel
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rdclient.daemon.FrontendMarkupAdapterListener
import com.jetbrains.rdclient.daemon.highlighters.MarkupListenerAggregator

class DocCommentsFoldingAggregator(
  project: Project,
  document: Document
) : MarkupListenerAggregator(project, document) {
  override fun canHandleEditor(editor: EditorImpl): Boolean {
    return true
  }

  override fun createListener(lifetime: Lifetime, editor: EditorImpl): FrontendMarkupAdapterListener {
    return DocCommentsFoldingAdapter(editor)
  }
}

class DocCommentsFoldingAdapter(private val editor: EditorImpl) : FrontendMarkupAdapterListener {
  companion object {
    private val commentKey = Key<CommentBase>("HighlightersComment")

    private fun executeOverDocHighlighters(
      highlighters: List<RangeHighlighterEx>,
      action: (RangeHighlighterEx, RdCommentFoldingModel) -> Unit
    ) {
      for (highlighter in highlighters) {
        val foldingModel = highlighter.getUserData(DocCommentModelKey)
        if (foldingModel != null) {
          action(highlighter, foldingModel)
        }
      }
    }
  }


  private val rangeMarkerCache = CommentsRangeMarkersCache()
  private val settings = RiderIntelligentCommentsSettingsProvider.getInstance()


  override fun afterAdded(highlighter: RangeHighlighterEx) {
  }

  override fun afterUpdated(highlighter: RangeHighlighterEx) {
  }

  override fun attributesChanged(
    highlighter: RangeHighlighterEx,
    renderersChanged: Boolean,
    fontStyleOrColorChanged: Boolean
  ) {
  }

  override fun afterBulkAdd(highlighters: List<RangeHighlighterEx>) {
    editor.project?.let {
      application.executeOnPooledThread {
        val controller = it.getComponent(RiderCommentsController::class.java) ?: return@executeOnPooledThread
        val commentsCreator = it.service<RiderCommentsCreator>()

        rangeMarkerCache.invalidateAndSort()

        application.invokeLater {
          executeOverDocHighlighters(highlighters) { highlighter, model ->
            val marker = getRangeMarkerFor(highlighter.range, highlighter.document)
            marker.isGreedyToRight = true

            val comment = commentsCreator.tryCreateComment(model.comment, editor, marker)
            comment ?: return@executeOverDocHighlighters
            controller.addComment(editor, comment)

            highlighter.putUserData(commentKey, comment)
            highlighter.isGreedyToRight = true

            if (settings.commentsDisplayKind.value != CommentsDisplayKind.Code) {
              highlighter.gutterIconRenderer = DocCommentSwitchRenderModeGutterMark(comment, editor, it)
            }
          }
        }
      }
    }
  }

  private fun getRangeMarkerFor(range: TextRange, document: Document): RangeMarker {
    val deletedRangeMarker = rangeMarkerCache.tryGetFor(range)
    if (deletedRangeMarker != null) return deletedRangeMarker

    return document.createRangeMarker(range)
  }

  override fun attributesChanged(
    highlighter: RangeHighlighterEx,
    renderersChanged: Boolean,
    fontStyleChanged: Boolean,
    foregroundColorChanged: Boolean
  ) {
  }

  override fun beforeBulkRemove(highlighters: List<RangeHighlighterEx>) {
    editor.project?.let {
      executeOverDocHighlighters(highlighters) { highlighter, _ ->
        val existingComment = highlighter.getUserData(commentKey)

        if (existingComment != null) {
          highlighter.putUserData(commentKey, null)
          rangeMarkerCache.store(existingComment.rangeMarker)
        }
      }
    }
  }

  override fun beforeRemoved(highlighter: RangeHighlighterEx) {
  }
}