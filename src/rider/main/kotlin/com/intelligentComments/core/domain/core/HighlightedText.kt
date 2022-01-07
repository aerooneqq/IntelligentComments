package com.intelligentComments.core.domain.core

interface HighlightedText : Parentable {
  val text: String
  val highlighters: Collection<TextHighlighter>

  fun mergeWith(other: HighlightedText): HighlightedText
  fun mergeWith(rawText: String): HighlightedText
  fun ensureThatAllLinesAreNoLongerThan(maxLineLength: Int)
}