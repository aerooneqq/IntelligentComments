package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.IntelligentComment
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project

class IntelligentCommentUiModel(project: Project,
                                val comment: IntelligentComment) : UiInteractionModelBase(project) {
    private val myAuthors = mutableListOf<AuthorUiModel>()
    private val myReferences = mutableListOf<ReferenceUiModel>()
    private val myInvariants = mutableListOf<InvariantUiModel>()

    val authorsSection: SectionUiModel<AuthorUiModel>
    val contentSection: SectionWithHeaderUiModel<ContentSegmentUiModel>
    val referencesSection: SectionWithHeaderUiModel<ReferenceUiModel>
    val invariantsSection: SectionWithHeaderUiModel<InvariantUiModel>


    init {
        for (author in comment.allAuthors) myAuthors.add(AuthorUiModel.getFrom(project, author))
        val content = IntelligentCommentContentUiModel(project, comment.content)
        for (reference in comment.references) myReferences.add(ReferenceUiModel.getFrom(project, reference))
        for (invariant in comment.invariants) myInvariants.add(InvariantUiModel.getFrom(project, invariant))

        authorsSection = SectionUiModel(project, myAuthors)

        val contentHeaderText = HeaderTextInfo("Content: ", "Content (click to expand)")
        contentSection = SectionWithHeaderUiModel(project, content.segments, AllIcons.FileTypes.Text, contentHeaderText)

        val referenceHeaderText = HeaderTextInfo("References: ", "References (click to expand)")
        referencesSection = SectionWithHeaderUiModel(project, myReferences, AllIcons.FileTypes.Java, referenceHeaderText)

        val invariantHeaderText = HeaderTextInfo("Invariants: ", "Invariants (click to expand) ")
        invariantsSection = SectionWithHeaderUiModel(project, myInvariants, AllIcons.Nodes.Interface, invariantHeaderText)
    }
}