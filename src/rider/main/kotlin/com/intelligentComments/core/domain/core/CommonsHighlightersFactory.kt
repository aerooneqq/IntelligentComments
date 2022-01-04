package com.intelligentComments.core.domain.core

import java.awt.Color

object CommonsHighlightersFactory {
  fun getWithRoundedBackgroundRect(
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
}