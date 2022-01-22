package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.core.domain.core.Parentable
import com.intelligentComments.core.domain.core.TextHighlighter
import com.intelligentComments.core.domain.core.TextHighlighterImpl

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
  ) : this(text, parent) {
    attachHighlighters(highlighters)
    myHighlighters.addAll(highlighters ?: listOf())
  }

  constructor(
    text: String,
    parent: Parentable?,
    highlighter: TextHighlighter?
  ) : this(text, parent) {
    if (highlighter != null) {
      attachHighlighter(highlighter)
      myHighlighters.add(highlighter)
    }
  }


  private fun attachHighlighters(highlighters: Collection<TextHighlighter>?) {
    if (highlighters == null) return

    for (highlighter in highlighters) {
      attachHighlighter(highlighter)
    }
  }

  private fun attachHighlighter(highlighter: TextHighlighter?) {
    highlighter as TextHighlighterImpl
    highlighter.parent = this
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

  override fun ensureThatAllLinesAreNoLongerThan(maxLineLength: Int) {
    val indicesOfLineBreaks = mutableListOf(0)
    for (i in text.indices) {
      if (text[i] == '\n') {
        indicesOfLineBreaks.add(i)
      }
    }

    indicesOfLineBreaks.add(text.length - 1)
    val positionsOfAdditionalLineBreaks = mutableListOf<Int>()

    var index = 0
    while (index < indicesOfLineBreaks.size - 1) {
      val left = indicesOfLineBreaks[index]
      val right = indicesOfLineBreaks[index + 1]
      val lineLength = right - left
      if (lineLength <= maxLineLength) {
        ++index
        continue
      }

      var i = left + maxLineLength
      while (i > left) {
        if (text[i] == ' ') {
          positionsOfAdditionalLineBreaks.add(i)
          indicesOfLineBreaks.add(index, i)
          ++index
          break
        }

        --i
      }

      if (i == left) {
        i = left + maxLineLength
        while (i < right) {
          if (text[i] == ' ') {
            positionsOfAdditionalLineBreaks.add(i)
            indicesOfLineBreaks.add(i)
            ++index
            break
          }

          ++i
        }
      }

      ++index
    }

    val sb = StringBuilder(text)
    for (i in positionsOfAdditionalLineBreaks.indices) {
      sb[positionsOfAdditionalLineBreaks[i]] = '\n'
    }

    text = sb.toString()
  }
}