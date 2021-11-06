package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.ExceptionSegment
import com.intelligentComments.core.domain.core.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.project.Project

class ExceptionUiModel(project: Project,
                       exceptionSegment: ExceptionSegment) : ContentSegmentUiModel(project, exceptionSegment) {
    val name = HighlightedTextUiWrapper(project, HighlightedTextImpl(exceptionSegment.name, emptyList()))
    val content = ContentSegmentsUiModel(project, exceptionSegment.content)
}