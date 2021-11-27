package com.intelligentComments.core.domain.core

interface HighlightedText {
  val text: String
  val highlighters: Collection<TextHighlighter>
}

class HighlightedTextImpl(
  override val text: String,
  override val highlighters: Collection<TextHighlighter>
) : HighlightedText