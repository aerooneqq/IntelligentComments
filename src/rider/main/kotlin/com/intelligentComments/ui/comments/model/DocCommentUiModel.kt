package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.DocCommentRenderer
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.project.Project

class DocCommentUiModel(private val docComment: DocComment, project: Project) : UiInteractionModelBase(project), RootUiModel {
    val contentSection: SectionUiModel<ContentSegmentUiModel>


    init {
        val segments = docComment.content.segments.map { ContentSegmentUiModel.getFrom(project, it) }
        contentSection = SectionUiModel(project, segments)
    }


    override fun getRenderer(project: Project): EditorCustomElementRenderer {
        return DocCommentRenderer(this)
    }
}