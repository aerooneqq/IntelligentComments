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

class TextHighlighterFromRd(val project: Project,
                            private val highlighter: RdTextHighlighter) : UniqueEntityImpl(), TextHighlighter {
    override val startOffset: Int = highlighter.startOffset
    override val endOffset: Int = highlighter.endOffset
    override val textColor: Color
        get() = project.service<ColorsProvider>().getColorFor(ColorName(highlighter.key))

    private var cachedAttributes: TextAttributes? = null
    override val attributes: TextAttributes
        get() {
            var attributes = cachedAttributes
            if (attributes == null) {
                attributes = highlighter.attributes.toIntelligentCommentsAttributes()
                cachedAttributes = attributes
                return attributes
            }

            return attributes
        }

    override val backgroundStyle: BackgroundStyle? = if (highlighter.backgroundStyle != null) {
        BackgroundStyleFromRd(highlighter.backgroundStyle)
    } else {
        null
    }

    override val mouseInOutAnimation: MouseInOutAnimation? = highlighter.animation?.toTextAnimation()
}

class BackgroundStyleFromRd(rdBackgroundStyle: RdBackgroundStyle) : BackgroundStyle {
    override val backgroundColor: Color = Color.decode(rdBackgroundStyle.backgroundColor.hex)
    override val roundedRect: Boolean = rdBackgroundStyle.roundedRect
}

fun RdTextAnimation.toTextAnimation(): MouseInOutAnimation {
    return when(this) {
        is RdUnderlineTextAnimation -> UnderlineTextAnimation()
        else -> throw IllegalArgumentException()
    }
}

fun RdTextAttributes.toIntelligentCommentsAttributes(): TextAttributesImpl {
    val underline = underline ?: false
    val weight = fontWeight ?: TextAttribute.WEIGHT_REGULAR
    val style = when(fontStyle) {
        RdFontStyle.Regular -> Font.PLAIN
        RdFontStyle.Bold -> Font.BOLD
        else -> Font.PLAIN
    }

    return TextAttributesImpl(underline, weight, style)
}