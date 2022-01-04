package com.intelligentComments.core.domain.core

interface HighlightedText : Parentable {
  val text: String
  val highlighters: Collection<TextHighlighter>

  fun mergeWith(other: HighlightedText): HighlightedText
  fun mergeWith(rawText: String): HighlightedText
}

class HighlightedTextImpl : HighlightedText {
  companion object {
    fun createEmpty(parent: Parentable?) = HighlightedTextImpl("", parent)
  }

  override val parent: Parentable?
  override var text: String
    private set

  private var myHighlighters = mutableListOf<TextHighlighter>()
  override val highlighters: Collection<TextHighlighter>
    get() = myHighlighters


  constructor(
    text: String,
    parent: Parentable?
  ) {
    this.text = text
    this.parent = parent
  }

  constructor(
    text: String,
    parent: Parentable?,
    highlighters: Collection<TextHighlighter>?
  ) {
    attachHighlighters(highlighters)
    myHighlighters.addAll(highlighters ?: listOf())
    this.text = text
    this.parent = parent
  }


  private fun attachHighlighters(highlighters: Collection<TextHighlighter>?) {
    if (highlighters == null) return

    for (highlighter in highlighters) {
      highlighter as TextHighlighterImpl
      highlighter.parent = this
    }
  }


  override fun mergeWith(other: HighlightedText): HighlightedText {
    val length = text.length
    text += other.text
    attachHighlighters(other.highlighters)
    myHighlighters.addAll(other.highlighters.map { it.shift(length) })
    return this
  }

  override fun mergeWith(rawText: String): HighlightedText {
    text += rawText
    return this
  }
}