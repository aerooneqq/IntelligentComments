package com.intelligentcomments.core.domain.rd

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentcomments.ui.colors.ColorName
import com.intelligentcomments.ui.colors.ColorsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.jetbrains.ide.model.highlighterRegistration.IdeaTextAttributesKey
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.daemon.TextAttributesRegistrationHost
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute


fun RdTextHighlighter.toIdeaHighlighter(project: Project, parent: Parentable?): TextHighlighter {
  if (isResharperHighlighter == true) return toIdeaHighlighterFromReSharper(project, parent)

  return toIdeaHighlighterInternal(project, parent, null)
}

private fun RdTextHighlighter.toIdeaHighlighterInternal(
  project: Project,
  parent: Parentable?,
  textColor: Color?
): TextHighlighter {
  val finalTextColor = textColor ?: project.service<ColorsProvider>().getColorFor(ColorName(key))

  var attributes = attributes.toIntelligentCommentsAttributes()

  val settings = RiderIntelligentCommentsSettingsProvider.getInstance()
  if (settings.useItalicFont.value) {
    attributes = attributes.copy(style = Font.ITALIC)
  }

  val mouseInOutAnimation = animation?.toTextAnimation(finalTextColor, project)
  val references = references?.map { ReferenceFromRd.getFrom(project, it) } ?: emptyList()
  val ideaSquiggles = errorSquiggles?.toIdeaSquiggles()
  val backgroundStyle = if (backgroundStyle != null) {
    BackgroundStyleFromRd(backgroundStyle)
  } else {
    null
  }

  return TextHighlighterImpl(
     parent, startOffset, endOffset, finalTextColor, references, attributes, backgroundStyle, mouseInOutAnimation, ideaSquiggles)
}

private fun RdTextHighlighter.toIdeaHighlighterFromReSharper(
  project: Project,
  parent: Parentable?,
): TextHighlighter {
  val textAttributes = tryGetTextAttributes(key) ?: return toIdeaHighlighterInternal(project, parent, null)

  var textColor = textAttributes.foregroundColor?.adjustColorToTheme()
  textColor = textColor ?: return toIdeaHighlighterInternal(project, parent, null)
  return toIdeaHighlighterInternal(project, parent, textColor)
}

fun Color.adjustColorToTheme(): Color {
  return if (EditorColorsManager.getInstance().isDarkEditor) {
    darker()
  } else {
    brighter()
  }
}

fun tryGetTextAttributes(key: String): TextAttributes? {
  val scheme = EditorColorsManager.getInstance().globalScheme
  val ideaTextAttributesKey = IdeaTextAttributesKey(null, key)

  val host = TextAttributesRegistrationHost.getInstance()
  return host.getTextAttributes(ideaTextAttributesKey, scheme)
}

class BackgroundStyleFromRd(rdBackgroundStyle: RdBackgroundStyle) : BackgroundStyle {
  override val backgroundColor: Color = rdBackgroundStyle.backgroundColor.toIdeaColor()
  override val roundedRect: Boolean = rdBackgroundStyle.roundedRect
  override val leftRightPadding: Int = rdBackgroundStyle.leftRightMargin
}

fun RdColor.toIdeaColor(): Color {
  return Color.decode(hex)
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
    RdFontStyle.Italic -> Font.ITALIC
    else -> Font.PLAIN
  }

  return TextAttributesImpl(underline, weight, style)
}

fun RdSquiggles.toIdeaSquiggles(): Squiggles {
  return SquigglesImpl(kind.toRdSquigglesKind(), colorKey)
}

fun RdSquigglesKind.toRdSquigglesKind(): SquigglesKind {
  return when(this) {
    RdSquigglesKind.Dotted -> SquigglesKind.Dotted
    RdSquigglesKind.Wave -> SquigglesKind.Wave
  }
}