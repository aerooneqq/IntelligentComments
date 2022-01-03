package com.intelligentComments.core.domain.core

interface HighlightedText : Parentable {
  val text: String
  val highlighters: Collection<TextHighlighter>

  fun mergeWith(other: HighlightedText): HighlightedText
  fun mergeWith(rawText: String): HighlightedText
}

class HighlightedTextImpl(
  text: String,
  override val parent: Parentable?,
  highlighters: Collection<TextHighlighter>
) : HighlightedText {
  companion object {
    fun createEmpty(parent: Parentable?) = HighlightedTextImpl("", parent, emptyList())
  }

  override var text: String = text
    private set

  private var myHighlighters = mutableListOf<TextHighlighter>()
  override val highlighters: Collection<TextHighlighter>
    get() = myHighlighters


  init {
    myHighlighters.addAll(highlighters)
  }


  override fun mergeWith(other: HighlightedText): HighlightedText {
    val length = text.length
    text += other.text
    myHighlighters.addAll(other.highlighters.map { it.shift(length) })
    return this
  }

  override fun mergeWith(rawText: String): HighlightedText {
    text += rawText
    return this
  }
}