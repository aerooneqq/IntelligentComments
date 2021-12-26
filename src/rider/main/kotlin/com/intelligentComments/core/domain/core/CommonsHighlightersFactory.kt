package com.intelligentComments.core.domain.core

import java.awt.Color

object CommonsHighlightersFactory {
  fun getWithRoundedBackgroundRect(
    textColor: Color,
    backgroundColor: Color,
    textLength: Int
  ): TextHighlighter {
    return TextHighlighterImpl(
      0,
      textLength,
      textColor,
      backgroundStyle = BackgroundStyleImpl(backgroundColor, true, 2)
    )
  }
}