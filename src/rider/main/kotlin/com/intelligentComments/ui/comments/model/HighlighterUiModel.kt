package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.TextHighlighter
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project

class HighlighterUiModel(project: Project,
                         private val highlighter: TextHighlighter) : UiInteractionModelBase(project) {
    companion object {
        fun getFor(project: Project, highlighter: TextHighlighter): HighlighterUiModel {
            return HighlighterUiModel(project, highlighter)
        }
    }

    val startOffset = highlighter.startOffset
    val endOffset = highlighter.endOffset
    val weight = highlighter.attributes.weight
    val style = highlighter.attributes.style

    var textColor  = highlighter.textColor
    var backgroundStyle = highlighter.backgroundStyle
    var underline: Boolean = highlighter.attributes.underline

    override fun handleMouseIn(e: EditorMouseEvent): Boolean = applyMouseInOutAnimation(true)

    private fun applyMouseInOutAnimation(mouseIn: Boolean): Boolean {
        val result = highlighter.mouseInOutAnimation?.applyTo(this, mouseIn)
        if (result != null) {
            myMouseIn = mouseIn
            return result
        }

        return false
    }

    override fun handleMouseOut(e: EditorMouseEvent): Boolean = applyMouseInOutAnimation(false)
}