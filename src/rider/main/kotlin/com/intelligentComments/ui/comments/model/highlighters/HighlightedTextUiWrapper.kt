package com.intelligentComments.ui.comments.model.highlighters

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HighlightedTextUiWrapper(project: Project, highlightedText: HighlightedText) {
    val text = highlightedText.text
    val highlighters = highlightedText.highlighters.map { HighlighterUiModel.getFor(project, it) }

    override fun hashCode(): Int = (text.hashCode() * HashUtil.calculateHashFor(highlighters)) % HashUtil.mod
    override fun equals(other: Any?): Boolean = other is HighlightedTextUiWrapper && other.hashCode() == hashCode()
}