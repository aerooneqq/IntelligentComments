package com.intelligentComments.core.domain.core

interface IntelligentComment : CommentBase {
    val allAuthors: Collection<CommentAuthor>
    val content: IntelligentCommentContent
    val references: Collection<Reference>
    val invariants: Collection<Invariant>
    val todos: Collection<ToDo>
    val hacks: Collection<Hack>
}