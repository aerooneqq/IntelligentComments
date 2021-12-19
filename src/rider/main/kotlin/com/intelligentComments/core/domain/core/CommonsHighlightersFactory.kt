package com.intelligentComments.core.domain.core

import java.awt.Color
import java.util.*

object CommonsHighlightersFactory {
  fun getWithRoundedBackgroundRect(textColor: Color, backgroundColor: Color, textLength: Int): TextHighlighter {
    return object : DefaultTextHighlighter(0, textLength, textColor) {
      override val backgroundStyle: BackgroundStyle = BackgroundStyleImpl(backgroundColor, true, 2)
    }
  }
}