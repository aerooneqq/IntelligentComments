package com.intelligentComments.core.markup

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.utils.toGreedy
import com.intelligentComments.ui.comments.renderers.DocCommentSwitchRenderModeGutterMark
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.impl.source.tree.injected.changesHandler.range
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
      val controller = it.getComponent(RiderCommentsController::class.java) ?: return
      val commentsCreator = it.service<RiderCommentsCreator>()

      executeOverDocHighlighters(highlighters) { highlighter, model ->
        val marker = highlighter.document.createRangeMarker(highlighter.range)
        marker.toGreedy()

        val comment = commentsCreator.tryCreateComment(model.comment, editor, marker) ?: return@executeOverDocHighlighters
        controller.addComment(editor, comment)

        highlighter.gutterIconRenderer = DocCommentSwitchRenderModeGutterMark(comment, editor, it)
        highlighter.isGreedyToLeft = true
        highlighter.isGreedyToRight = true
      }
    }
  }

  override fun attributesChanged(
    highlighter: RangeHighlighterEx,
    renderersChanged: Boolean,
    fontStyleChanged: Boolean,
    foregroundColorChanged: Boolean
  ) {
  }

  override fun beforeBulkRemove(highlighters: List<RangeHighlighterEx>) {
  }

  override fun beforeRemoved(highlighter: RangeHighlighterEx) {
  }
}