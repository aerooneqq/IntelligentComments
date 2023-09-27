package com.intelligentcomments.core.domain.impl

import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.core.domain.core.Parentable
import com.intelligentcomments.core.domain.core.TextHighlighter
import com.intelligentcomments.core.domain.core.TextHighlighterImpl
import com.jetbrains.rd.platform.util.getLogger

class HighlightedTextImpl : HighlightedText {
  companion object {
    private val logger = getLogger<HighlightedTextImpl>()

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

  override fun addHighlightersFrom(other: HighlightedText): HighlightedText {
    assert(other.text == text)
    for (otherHighlighter in other.highlighters) {
      val otherHighlighterCopy = otherHighlighter.copy()
      attachHighlighter(otherHighlighterCopy)
      myHighlighters.add(otherHighlighterCopy)
    }

    myHighlighters.sortBy { it.startOffset }
    assertState()
    return this
  }

  private fun assertState() {
    for (i in 1 until myHighlighters.size) {
      if (myHighlighters[i - 1].endOffset > myHighlighters[i].startOffset) {
        logger.error("myHighlighters[i - 1].endOffset > myHighlighters[i].startOffset")
      }
    }
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

      var incrementedIndex = false
      var i = left + maxLineLength
      while (i > left) {
        if (text[i] == ' ') {
          positionsOfAdditionalLineBreaks.add(i)
          indicesOfLineBreaks.add(++index, i)
          incrementedIndex = true
          break
        }

        --i
      }

      if (i == left) {
        i = left + maxLineLength
        while (i < right) {
          if (text[i] == ' ') {
            positionsOfAdditionalLineBreaks.add(i)
            indicesOfLineBreaks.add(++index, i)
            incrementedIndex = true
            break
          }

          ++i
        }
      }

      if (!incrementedIndex) {
        ++index
      }
    }

    val sb = StringBuilder(text)
    for (i in positionsOfAdditionalLineBreaks.indices) {
      sb[positionsOfAdditionalLineBreaks[i]] = '\n'
    }

    text = sb.toString()
  }
}