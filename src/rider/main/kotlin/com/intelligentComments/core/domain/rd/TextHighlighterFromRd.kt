package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.ColorsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute


fun RdTextHighlighter.toIdeaHighlighter(project: Project): TextHighlighter {
  val textColor = project.service<ColorsProvider>().getColorFor(ColorName(key))
  val attributes = attributes.toIntelligentCommentsAttributes()
  val mouseInOutAnimation = animation?.toTextAnimation(textColor, project)
  val backgroundStyle = if (backgroundStyle != null) {
    BackgroundStyleFromRd(backgroundStyle)
  } else {
    null
  }

  return TextHighlighterImpl(startOffset, endOffset, textColor, attributes, backgroundStyle, mouseInOutAnimation)
}

class BackgroundStyleFromRd(rdBackgroundStyle: RdBackgroundStyle) : BackgroundStyle {
  override val backgroundColor: Color = Color.decode(rdBackgroundStyle.backgroundColor.hex)
  override val roundedRect: Boolean = rdBackgroundStyle.roundedRect
  override val leftRightPadding: Int = rdBackgroundStyle.leftRightMargin
}

fun RdTextAnimation.toTextAnimation(textColor: Color , project: Project): MouseInOutAnimation {
  val colorsProvider = project.service<ColorsProvider>()
  return when (this) {
    is RdUnderlineTextAnimation -> UnderlineTextAnimation()
    is RdForegroundColorAnimation -> ForegroundTextAnimation(Color.decode(this.hoveredColor.hex), textColor)
    is RdPredefinedForegroundColorAnimation -> ForegroundTextAnimation(
      colorsProvider.getColorFor(ColorName(this.key)),
      textColor
    )
    else -> throw IllegalArgumentException(this.javaClass.name)
  }
}

fun RdTextAttributes.toIntelligentCommentsAttributes(): TextAttributesImpl {
  val underline = underline ?: false
  val weight = fontWeight ?: TextAttribute.WEIGHT_REGULAR
  val style = when (fontStyle) {
    RdFontStyle.Regular -> Font.PLAIN
    RdFontStyle.Bold -> Font.BOLD
    else -> Font.PLAIN
  }

  return TextAttributesImpl(underline, weight, style)
}