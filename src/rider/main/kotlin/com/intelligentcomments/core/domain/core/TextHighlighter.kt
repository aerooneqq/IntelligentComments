package com.intelligentcomments.core.domain.core

import com.intelligentcomments.ui.comments.model.highlighters.HighlighterUiModel
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute


interface TextHighlighter : UniqueEntity, Parentable {
  val startOffset: Int
  val endOffset: Int
  val attributes: TextAttributes
  val textColor: Color
  val backgroundStyle: BackgroundStyle?
  val mouseInOutAnimation: MouseInOutAnimation?
  val references: Collection<Reference>
  val errorSquiggles: Squiggles?

  fun shift(delta: Int): TextHighlighter

  fun shiftInplace(delta: Int)
  fun extendEnd(delta: Int)

  fun copy(): TextHighlighter
}

class TextHighlighterImpl(
  parent: Parentable?,
  startOffset: Int,
  endOffset: Int,
  override val textColor: Color,
  override val references: Collection<Reference> = emptyList(),
  override val attributes: TextAttributes = TextAttributesImpl.defaultAttributes,
  override val backgroundStyle: BackgroundStyle? = null,
  override val mouseInOutAnimation: MouseInOutAnimation? = null,
  override val errorSquiggles: Squiggles? = null
) : UniqueEntityImpl(), TextHighlighter {

  override var parent: Parentable? = parent
    internal set

  override var startOffset: Int = startOffset
    private set

  override var endOffset: Int = endOffset
    private set

  override fun shift(delta: Int): TextHighlighter {
    return TextHighlighterImpl(
      parent,
      startOffset + delta,
      endOffset + delta,
      textColor,
      references,
      attributes,
      backgroundStyle,
      mouseInOutAnimation,
      errorSquiggles
    )
  }

  override fun extendEnd(delta: Int) {
    endOffset += delta
  }

  override fun shiftInplace(delta: Int) {
    startOffset += delta
    endOffset += delta
  }

  override fun copy(): TextHighlighter {
    return TextHighlighterImpl(
      parent,
      startOffset,
      endOffset,
      textColor,
      references,
      attributes,
      backgroundStyle,
      mouseInOutAnimation,
      errorSquiggles
    )
  }
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

interface Squiggles {
  val kind: SquigglesKind
  val colorKey: String
}

enum class SquigglesKind {
  Wave,
  Dotted
}

data class SquigglesImpl(
  override val kind: SquigglesKind,
  override val colorKey: String
) : Squiggles

interface TextAttributes {
  val underline: Boolean
  val weight: Float
  val style: Int
}

data class TextAttributesImpl(
  override val underline: Boolean,
  override val weight: Float,
  override val style: Int,
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

