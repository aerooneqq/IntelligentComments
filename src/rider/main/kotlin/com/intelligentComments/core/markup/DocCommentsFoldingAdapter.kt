package com.intelligentComments.core.markup

import com.intelligentComments.core.editors.RiderCommentsHost
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.daemon.FrontendMarkupAdapterListener
import com.jetbrains.rdclient.daemon.highlighters.MarkupListenerAggregator

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
                                           action: (RangeHighlighterEx, Int) -> Unit) {
      for (highlighter in highlighters) {
        val commentIdentifier = highlighter.getUserData(DocCommentIdentifierKey)
        if (commentIdentifier != null) {
          action(highlighter, commentIdentifier)
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
    val host = editor.project?.getComponent(RiderCommentsHost::class.java) ?: return

    executeOverDocHighlighters(highlighters) { _, id ->
      val def = LifetimeDefinition()
      highlightersLifetimes[id] = def
      host.renderDocComment(editor, id, def.lifetime)
    }
  }

  override fun attributesChanged(highlighter: RangeHighlighterEx, renderersChanged: Boolean, fontStyleChanged: Boolean, foregroundColorChanged: Boolean) {
  }

  override fun beforeBulkRemove(highlighters: List<RangeHighlighterEx>) {
    executeOverDocHighlighters(highlighters) { _, id ->
      highlightersLifetimes.remove(id)?.terminate()
    }
  }

  override fun beforeRemoved(highlighter: RangeHighlighterEx) {
  }
}