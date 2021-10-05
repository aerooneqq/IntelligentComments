package com.intelligentComments.core.domain.core

interface IntelligentCommentContent : UniqueEntity {
    val segments: Collection<ContentSegment>
}

interface ContentSegment : UniqueEntity {
}

interface TextContentSegment : ContentSegment {
    val text: String?
}