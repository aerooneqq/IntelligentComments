package com.intelligentComments.core.domain.core

import java.awt.Color
import java.util.*

object CommonsHighlightersFactory {
  fun getWithRoundedBackgroundRect(textColor: Color, backgroundColor: Color, textLength: Int): TextHighlighter {
    return object : TextHighlighter {
      override val startOffset: Int = 0
      override val endOffset: Int = textLength
      override val attributes: TextAttributes = TextAttributesImpl.defaultAttributes
      override val textColor: Color = textColor
      override val backgroundStyle: BackgroundStyle = BackgroundStyleImpl(backgroundColor, true, 2)
      override val mouseInOutAnimation: MouseInOutAnimation? = null
      override val id: UUID = UUID.randomUUID()
    }
  }
}