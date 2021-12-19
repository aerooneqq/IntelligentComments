package com.intelligentComments.core.domain.core

import com.intelligentComments.ui.comments.model.highlighters.HighlighterUiModel
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute


interface TextHighlighter : UniqueEntity {
  val startOffset: Int
  val endOffset: Int
  val attributes: TextAttributes
  val textColor: Color
  val backgroundStyle: BackgroundStyle?
  val mouseInOutAnimation: MouseInOutAnimation?
}

open class DefaultTextHighlighter(
  override val startOffset: Int,
  override val endOffset: Int,
  override val textColor: Color
) : UniqueEntityImpl(), TextHighlighter {
  override val attributes: TextAttributes = TextAttributesImpl.defaultAttributes
  override val backgroundStyle: BackgroundStyle? = null
  override val mouseInOutAnimation: MouseInOutAnimation? = null
}

interface BackgroundStyle {
  val backgroundColor: Color
  val roundedRect: Boolean
  val leftRightPadding: Int
}

class BackgroundStyleImpl(
  override val backgroundColor: Color,
  override val roundedRect: Boolean,
  override val leftRightPadding: Int
) : BackgroundStyle

interface MouseInOutAnimation {
  fun applyTo(uiModel: HighlighterUiModel, mouseIn: Boolean): Boolean
}

interface TextAttributes {
  val underline: Boolean
  val weight: Float
  val style: Int
}

data class TextAttributesImpl(
  override val underline: Boolean,
  override val weight: Float,
  override val style: Int
) : TextAttributes {
  companion object {
    val defaultAttributes = TextAttributesImpl(false, TextAttribute.WEIGHT_REGULAR, Font.PLAIN)
  }
}

class UnderlineTextAnimation : MouseInOutAnimation {
  override fun applyTo(uiModel: HighlighterUiModel, mouseIn: Boolean): Boolean {
    uiModel.underline = mouseIn
    return true
  }
}

class ForegroundTextAnimation(
  private val hoveredColor: Color,
  private val originalColor: Color
) : MouseInOutAnimation {
  override fun applyTo(uiModel: HighlighterUiModel, mouseIn: Boolean): Boolean {
    uiModel.textColor = if (mouseIn) hoveredColor else originalColor
    return true
  }
}

