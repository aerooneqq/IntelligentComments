package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.renderers.IntelligentCommentsRenderer
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.util.Range
import com.jetbrains.rd.ide.model.RdIntelligentComment
import java.util.*

class IntelligentCommentFromRd(private val rdComment: RdIntelligentComment,
                               private val project: Project) : UniqueEntityImpl(), IntelligentComment {
    override val allAuthors: Collection<CommentAuthor> = createAuthors()
    override val content: IntelligentCommentContent = createContent(project)
    override val references: Collection<Reference> = createReferences()
    override val invariants: Collection<Invariant> = createInvariants()
    override val hacks: Collection<Hack> = createHacks()
    override val todos: Collection<ToDo> = createToDos()


    private fun createAuthors(): List<AuthorFromRd> {
        return rdComment.authors?.map { AuthorFromRd(it) } ?: emptyList()
    }

    private fun createContent(project: Project) = IntelligentCommentContentFromRd(rdComment.content, project)
    private fun createReferences(): List<ReferenceFromRd> = rdComment.references?.map { ReferenceFromRd.getFrom(it) } ?: emptyList()
    private fun createInvariants(): List<InvariantFromRd> = rdComment.invariants?.map { InvariantFromRd.getFrom(it) } ?: emptyList()
    private fun createToDos(): List<ToDoFromRd> = rdComment.toDos?.map { ToDoFromRd.getFrom(it, project) } ?: emptyList()
    private fun createHacks(): List<HackFromRd> = rdComment.hacks?.map { HackFromRd.getFrom(it, project) } ?: emptyList()

    fun getRenderer(project: Project): EditorCustomElementRenderer {
        return IntelligentCommentsRenderer(IntelligentCommentUiModel(project, this))
    }

    override val underlyingTextRange = TextRange(rdComment.range.startOffset, rdComment.range.endOffset)
}