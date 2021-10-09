package com.intelligentComments.core.domain.core

import java.awt.Image

interface IntelligentCommentContent : UniqueEntity {
    val segments: Collection<ContentSegment>
}

interface ContentSegment : UniqueEntity {
}

interface TextContentSegment : ContentSegment {
    val text: String
    val highlighters: Collection<TextHighlighter>
}

interface ImageContentSegment : ContentSegment {
    val image: Image
    val description: String?
}