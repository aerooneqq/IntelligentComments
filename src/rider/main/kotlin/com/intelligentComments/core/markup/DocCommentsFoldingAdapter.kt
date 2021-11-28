package com.intelligentComments.core.markup

import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.domain.rd.DocCommentFromRd
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdDocCommentFoldingModel
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import com.jetbrains.rd.util.reactive.ViewableMap
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
      action: (RangeHighlighterEx, RdDocCommentFoldingModel) -> Unit
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

      executeOverDocHighlighters(highlighters) { highlighter, model ->
        val docComment = DocCommentFromRd(model.docComment, it, highlighter)
        controller.addComment(editor, docComment)
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