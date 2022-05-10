package com.intelligentcomments.core.markup

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Key
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rd.ide.model.RdCommentFoldingModel
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelHandler

val DocCommentModelKey = Key<RdCommentFoldingModel>("DocCommentModel")

class DocCommentsFoldingHandler : IProtocolHighlighterModelHandler {
  override fun accept(model: HighlighterModel): Boolean {
    return model is RdCommentFoldingModel
  }

  override fun compare(model: HighlighterModel, highlighter: RangeHighlighter): Boolean {
    if (model !is RdCommentFoldingModel) return false
    if (!highlighter.isValid) return false
    if (highlighter.startOffset != model.start || highlighter.endOffset != model.end) return false

    return true
  }

  override fun initialize(model: HighlighterModel, highlighter: RangeHighlighter) {
    model as RdCommentFoldingModel
    highlighter.putUserData(DocCommentModelKey, model)
  }

  override fun move(startOffset: Int, endOffset: Int, model: HighlighterModel): HighlighterModel? {
    return (model as RdCommentFoldingModel).run {
      RdCommentFoldingModel(
        commentIdentifier,
        comment,
        layer,
        isExactRange,
        documentVersion,
        isGreedyToLeft,
        isGreedyToRight,
        isThinErrorStripeMark,
        textToHighlight,
        textAttributesKey,
        id,
        properties,
        startOffset,
        endOffset
      )
    }
  }
}