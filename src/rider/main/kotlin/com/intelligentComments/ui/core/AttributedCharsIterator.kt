package com.intelligentComments.ui.core

import com.intelligentComments.ui.comments.model.highlighters.HighlighterUiModel
import com.intelligentComments.ui.util.TextUtil
import java.awt.Font
import java.awt.font.TextAttribute
import java.text.AttributedCharacterIterator
import java.text.CharacterIterator

class AttributedCharsIterator(
  private val chars: CharArray,
  private val from: Int,
  private val to: Int,
  private val highlighter: HighlighterUiModel
) : AttributedCharacterIterator {
  private var current = from
  private val attributes = HashMap<AttributedCharacterIterator.Attribute, Any>()


  init {
    if (highlighter.underline) {
      attributes[TextAttribute.UNDERLINE] = TextAttribute.UNDERLINE_LOW_TWO_PIXEL
    }

    attributes[TextAttribute.FOREGROUND] = highlighter.textColor
    attributes[TextAttribute.WEIGHT] = highlighter.weight
    attributes[TextAttribute.FONT] = when (highlighter.style) {
      Font.BOLD -> TextUtil.boldFont
      else -> TextUtil.font
    }
  }


  override fun clone(): Any = AttributedCharsIterator(chars, from, to, highlighter)
  override fun first(): Char = chars[from]
  override fun last(): Char = chars[to - 1]
  override fun current(): Char = chars[current]

  override fun next(): Char {
    ++current
    if (current >= to) {
      current = to
      return CharacterIterator.DONE
    }

    return chars[current]
  }

  override fun previous(): Char {
    --current
    if (current < from) {
      current = from
      return CharacterIterator.DONE
    }

    return chars[current]
  }

  override fun setIndex(position: Int): Char {
    if (position in from until to) {
      current = position
      return current()
    }

    throw IllegalArgumentException()
  }

  override fun getBeginIndex(): Int = from
  override fun getEndIndex(): Int = to
  override fun getIndex(): Int = current
  override fun getRunStart(): Int = beginIndex
  override fun getRunStart(attribute: AttributedCharacterIterator.Attribute?): Int = runStart
  override fun getRunStart(attributes: MutableSet<out AttributedCharacterIterator.Attribute>?): Int = runStart
  override fun getRunLimit(): Int = endIndex
  override fun getRunLimit(attribute: AttributedCharacterIterator.Attribute?): Int = runLimit
  override fun getRunLimit(attributes: MutableSet<out AttributedCharacterIterator.Attribute>?): Int = runLimit
  override fun getAttributes(): MutableMap<AttributedCharacterIterator.Attribute, Any> = attributes

  override fun getAttribute(attribute: AttributedCharacterIterator.Attribute?): Any? {
    if (attribute == null) return null
    return attributes[attribute]
  }

  override fun getAllAttributeKeys(): MutableSet<AttributedCharacterIterator.Attribute> = attributes.keys
}