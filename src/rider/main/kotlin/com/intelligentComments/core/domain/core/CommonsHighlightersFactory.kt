package com.intelligentComments.core.domain.core

import com.intelligentComments.core.domain.rd.BackendHighlightersKeys
import com.intelligentComments.core.domain.rd.tryGetTextAttributes
import java.awt.Color

object CommonsHighlightersFactory {
  fun createWithRoundedBackgroundRect(
    parent: Parentable?,
    textColor: Color,
    backgroundColor: Color,
    textLength: Int
  ): TextHighlighter {
    return TextHighlighterImpl(
      parent,
      0,
      textLength,
      textColor,
      backgroundStyle = BackgroundStyleImpl(backgroundColor, true, 2)
    )
  }

  fun tryCreateCommentHighlighter(
    parent: Parentable?,
    textLength: Int
  ): TextHighlighter? {
    return createHighlighter(parent, BackendHighlightersKeys.commentKey, textLength)
  }

  private fun createHighlighter(
    parent: Parentable?,
    key: String,
    textLength: Int
  ): TextHighlighter? {
    val attributes = tryGetTextAttributes(key) ?: return null

    return TextHighlighterImpl(
      parent,
      0,
      textLength,
      attributes.foregroundColor.darker(),
      attributes = TextAttributesImpl.defaultAttributes
    )
  }

  fun createHighlighter(
    length: Int,
    color: Color,
    attributes: TextAttributes = TextAttributesImpl.defaultAttributes
  ): TextHighlighter {
    return TextHighlighterImpl(
      null,
      0,
      length,
      color,
      attributes = attributes
    )
  }
}