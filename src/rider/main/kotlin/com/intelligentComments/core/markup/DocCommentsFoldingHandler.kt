package com.intelligentComments.core.markup

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Key
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rd.ide.model.RdDocCommentFoldingModel
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelHandler

val DocCommentModelKey = Key<RdDocCommentFoldingModel>("DocCommentModel")

class DocCommentsFoldingHandler : IProtocolHighlighterModelHandler {
  override fun accept(model: HighlighterModel): Boolean {
    return model is RdDocCommentFoldingModel
  }

  override fun compare(model: HighlighterModel, highlighter: RangeHighlighter): Boolean {
    if (model !is RdDocCommentFoldingModel) return false
    if (!highlighter.isValid) return false
    if (highlighter.startOffset != model.start || highlighter.endOffset != model.end) return false

    return true
  }

  override fun initialize(model: HighlighterModel, highlighter: RangeHighlighter) {
    model as RdDocCommentFoldingModel
    highlighter.putUserData(DocCommentModelKey, model)
  }

  override fun move(startOffset: Int, endOffset: Int, model: HighlighterModel): HighlighterModel? {
    return (model as RdDocCommentFoldingModel).run {
      RdDocCommentFoldingModel(
        commentIdentifier,
        docComment,
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