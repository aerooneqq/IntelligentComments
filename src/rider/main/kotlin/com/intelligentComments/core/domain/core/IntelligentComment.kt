package com.intelligentComments.core.domain.core

interface IntelligentComment : UniqueEntity {
    val allAuthors: Collection<CommentAuthor>
    val content: IntelligentCommentContent
    val references: Collection<Reference>
    val invariants: Collection<Invariant>
}