package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class IntelligentCommentContentUiModel(project: Project,
                                       content: IntelligentCommentContent) : UiInteractionModelBase(project) {
    private val mySegments = mutableListOf<ContentSegmentUiModel>()

    val segments: Collection<ContentSegmentUiModel> = mySegments

    init {
        for (segment in content.segments) mySegments.add(ContentSegmentUiModel.getFrom(project, segment))
    }

    override fun hashCode(): Int = HashUtil.calculateHashFor(mySegments)
    override fun equals(other: Any?): Boolean = other is IntelligentCommentContent && other.hashCode() == hashCode()
}