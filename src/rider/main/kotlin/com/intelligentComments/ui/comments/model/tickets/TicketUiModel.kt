package com.intelligentComments.ui.comments.model.tickets

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import java.awt.Color
import java.awt.Font
import java.awt.font.TextAttribute
import java.util.*

class TicketUiModel(private val ticket: Ticket, project: Project) : UiInteractionModelBase(project) {
    val nameText = HighlightedTextUiWrapper(project, HighlightedTextImpl(ticket.shortName, listOf(getNameHighlighter())))
    val url = ticket.url

    private fun getNameHighlighter(): TextHighlighter {
        val defaultColor = colorsProvider.getColorFor(Colors.TextUrlColor)
        val hoveredColor = colorsProvider.getColorFor(Colors.TextUrlColorHovered)

        return object : TextHighlighter {
            override val startOffset: Int = 0
            override val endOffset: Int = ticket.shortName.length
            override val attributes: TextAttributes = TextAttributesImpl(true, TextAttribute.WEIGHT_REGULAR, Font.PLAIN)
            override val textColor: Color = defaultColor
            override val backgroundStyle: BackgroundStyle? = null
            override val mouseInOutAnimation: MouseInOutAnimation = ForegroundTextAnimation(hoveredColor, defaultColor)
            override val id: UUID = UUID.randomUUID()
        }
    }

    override fun hashCode(): Int = (ticket.shortName.hashCode() * url.hashCode()) % HashUtil.mod
    override fun equals(other: Any?): Boolean = other is TicketUiModel && other.hashCode() == hashCode()
}