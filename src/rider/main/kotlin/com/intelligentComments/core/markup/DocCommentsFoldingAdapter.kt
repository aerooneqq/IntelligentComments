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
import com.jetbrains.rider.xamarin.xcAssets.models.deserizlizeIconXcAssets

class DocCommentsFoldingAggregator(project: Project,
                                   document: Document) : MarkupListenerAggregator(project, document) {
  override fun canHandleEditor(editor: EditorImpl): Boolean {
    return true
  }

  override fun createListener(lifetime: Lifetime, editor: EditorImpl): FrontendMarkupAdapterListener {
    return DocCommentsFoldingAdapter(editor)
  }
}

class DocCommentsFoldingAdapter(private val editor: EditorImpl) : FrontendMarkupAdapterListener {
  companion object {
    private fun executeOverDocHighlighters(highlighters: List<RangeHighlighterEx>,
                                           action: (RangeHighlighterEx, RdDocCommentFoldingModel) -> Unit) {
      for (highlighter in highlighters) {
        val foldingModel = highlighter.getUserData(DocCommentModelKey)
        if (foldingModel != null) {
          action(highlighter, foldingModel)
        }
      }
    }
  }


  private val highlightersLifetimes = ViewableMap<Int, LifetimeDefinition>()


  override fun afterAdded(highlighter: RangeHighlighterEx) {
  }

  override fun afterUpdated(highlighter: RangeHighlighterEx) {
  }

  override fun attributesChanged(highlighter: RangeHighlighterEx, renderersChanged: Boolean, fontStyleOrColorChanged: Boolean) {
  }

  override fun afterBulkAdd(highlighters: List<RangeHighlighterEx>) {
    editor.project?.let {
      val controller = it.getComponent(RiderCommentsController::class.java) ?: return

      executeOverDocHighlighters(highlighters) { highlighter, model ->
        val def = LifetimeDefinition()
        highlightersLifetimes[model.commentIdentifier] = def

        val docComment = DocCommentFromRd(model.docComment, it, highlighter)
        controller.addComment(editor, docComment, def.lifetime)
      }
    }
  }

  override fun attributesChanged(highlighter: RangeHighlighterEx, renderersChanged: Boolean, fontStyleChanged: Boolean, foregroundColorChanged: Boolean) {
  }

  override fun beforeBulkRemove(highlighters: List<RangeHighlighterEx>) {
    executeOverDocHighlighters(highlighters) { _, model ->
      highlightersLifetimes.remove(model.commentIdentifier)?.terminate()
    }
  }

  override fun beforeRemoved(highlighter: RangeHighlighterEx) {
  }
}