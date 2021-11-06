package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.ParagraphContentSegment
import com.intellij.openapi.project.Project

class ParagraphUiModel(project: Project,
                       paragraph: ParagraphContentSegment) : ContentSegmentUiModel(project, paragraph) {
    val content = ContentSegmentsUiModel(project, paragraph.content)
}