package com.intelligentComments.core.domain.core

import com.intelligentComments.core.domain.rd.BackendHighlightersKeys
import com.intelligentComments.core.domain.rd.tryGetTextAttributes
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import java.awt.Color
import java.awt.Font

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
      attributes = getOrAdjustAttributes(),
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
      attributes = getOrAdjustAttributes()
    )
  }

  private fun getOrAdjustAttributes(existingAttributes: TextAttributes? = null): TextAttributes {
    val useItalicFont = RiderIntelligentCommentsSettingsProvider.getInstance().useItalicFont.value

    if (existingAttributes != null) {
      if (useItalicFont && existingAttributes is TextAttributesImpl) {
        return existingAttributes.copy(style = Font.ITALIC)
      }

      return existingAttributes
    }

    val attributes = TextAttributesImpl.defaultAttributes
    if (useItalicFont) {
      return attributes.copy(style = Font.ITALIC)
    }

    return attributes
  }

  fun createHighlighter(
    length: Int,
    color: Color,
    attributes: TextAttributes = TextAttributesImpl.defaultAttributes,
    references: List<Reference> = listOf()
  ): TextHighlighter {
    return TextHighlighterImpl(
      null,
      0,
      length,
      color,
      attributes = getOrAdjustAttributes(attributes),
      references = references
    )
  }
}