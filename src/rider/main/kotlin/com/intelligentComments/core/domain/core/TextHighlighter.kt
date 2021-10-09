package com.intelligentComments.core.domain.core

import com.intelligentComments.ui.comments.model.HighlighterUiModel
import java.awt.Color


interface TextHighlighter : UniqueEntity {
    val startOffset: Int
    val endOffset: Int
    val attributes: TextAttributes
    val textColor: Color
    val backgroundStyle: BackgroundStyle?
    val mouseInOutAnimation: MouseInOutAnimation?
}

interface BackgroundStyle {
    val backgroundColor: Color
    val roundedRect: Boolean
}

interface MouseInOutAnimation {
    fun applyTo(uiModel: HighlighterUiModel, mouseIn: Boolean): Boolean
}

interface TextAttributes {
    val underline: Boolean
    val weight: Float
    val style: Int
}

data class TextAttributesImpl(override val underline: Boolean,
                              override val weight: Float,
                              override val style: Int) : TextAttributes

class UnderlineTextAnimation : MouseInOutAnimation {
    override fun applyTo(uiModel: HighlighterUiModel, mouseIn: Boolean): Boolean {
        uiModel.underline = mouseIn
        return true
    }
}

