package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.ColorsProvider
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.jetbrains.ide.model.highlighterRegistration.IdeaTextAttributesKey
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.daemon.TextAttributesRegistrationHost
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute


fun RdTextHighlighter.toIdeaHighlighter(project: Project): TextHighlighter {
  if (isResharperHighlighter == true) return toIdeaHighlighterFromReSharper(project)

  return toIdeaHighlighterInternal(project, null)
}

private fun RdTextHighlighter.toIdeaHighlighterInternal(project: Project, textColor: Color?): TextHighlighter {
  val finalTextColor = textColor ?: project.service<ColorsProvider>().getColorFor(ColorName(key))

  val attributes = attributes.toIntelligentCommentsAttributes()
  val mouseInOutAnimation = animation?.toTextAnimation(finalTextColor, project)
  val backgroundStyle = if (backgroundStyle != null) {
    BackgroundStyleFromRd(backgroundStyle)
  } else {
    null
  }

  return TextHighlighterImpl(startOffset, endOffset, finalTextColor, attributes, backgroundStyle, mouseInOutAnimation)
}

private fun RdTextHighlighter.toIdeaHighlighterFromReSharper(project: Project): TextHighlighter {
  val scheme = EditorColorsManager.getInstance().globalScheme
  val key = IdeaTextAttributesKey(null, this.key)

  val host = TextAttributesRegistrationHost.getInstance()
  val textAttributes = host.getTextAttributes(key, scheme) ?: return toIdeaHighlighterInternal(project, null)

  val textColor = textAttributes.foregroundColor.darker()
  return toIdeaHighlighterInternal(project, textColor)
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