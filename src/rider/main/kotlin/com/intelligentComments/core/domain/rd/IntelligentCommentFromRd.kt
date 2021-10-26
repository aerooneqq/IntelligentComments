package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.renderers.IntelligentCommentsRenderer
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdIntelligentComment
import java.util.*

class IntelligentCommentFromRd(private val rdComment: RdIntelligentComment,
                               private val project: Project) : UniqueEntityImpl(), IntelligentComment {
    override val allAuthors: Collection<CommentAuthor> = createAuthors()
    override val content: IntelligentCommentContent = createContent(project)
    override val references: Collection<Reference> = createReferences()
    override val invariants: Collection<Invariant> = createInvariants()


    private fun createAuthors(): List<AuthorFromRd> {
        return rdComment.authors.map { AuthorFromRd(it) }
    }

    private fun createContent(project: Project): IntelligentCommentContent {
        return object : IntelligentCommentContent {
            private val myCachedSegments: Collection<ContentSegment>

            init {
                val segments = rdComment.content.valueOrNull?.content?.content
                myCachedSegments = segments?.map { ContentSegmentFromRd.getFrom(it, project) } ?: emptyList()
            }

            override val segments: Collection<ContentSegment> = myCachedSegments
            override val id: UUID = UUID.randomUUID()
        }
    }

    private fun createReferences(): List<ReferenceFromRd> {
        return rdComment.references.map { ReferenceFromRd.getFrom(it) }
    }

    private fun createInvariants(): List<InvariantFromRd> {
        return rdComment.invariants.map { InvariantFromRd.getFrom(it) }
    }

    fun getRenderer(project: Project): EditorCustomElementRenderer {
        return IntelligentCommentsRenderer(IntelligentCommentUiModel(project, this))
    }
}