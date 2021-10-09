package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intelligentComments.core.domain.core.TextContentSegment
import com.intellij.openapi.project.Project

class IntelligentCommentContentUiModel(project: Project,
                                       content: IntelligentCommentContent) : UiInteractionModelBase(project) {
    private val mySegments = mutableListOf<ContentSegmentUiModel>()

    val segments: Collection<ContentSegmentUiModel> = mySegments

    init {
        for (segment in content.segments) mySegments.add(ContentSegmentUiModel.getFrom(project, segment))
    }
}

open class ContentSegmentUiModel(project: Project,
                                 segment: ContentSegment) : UiInteractionModelBase(project) {
    companion object {
        fun getFrom(project: Project, segment: ContentSegment): ContentSegmentUiModel {
            return when(segment) {
                is TextContentSegment -> TextContentSegmentUiModel(project, segment)
                else -> throw IllegalArgumentException(segment.toString())
            }
        }
    }
}

class TextContentSegmentUiModel(project: Project,
                                textSegment: TextContentSegment) : ContentSegmentUiModel(project, textSegment) {
    val text = textSegment.text
    val highlighters = textSegment.highlighters.map { HighlighterUiModel.getFrom(project, it) }
}