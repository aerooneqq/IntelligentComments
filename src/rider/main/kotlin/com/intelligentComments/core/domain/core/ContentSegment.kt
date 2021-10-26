package com.intelligentComments.core.domain.core

import java.awt.Image

interface IntelligentCommentContent : UniqueEntity {
    val segments: Collection<ContentSegment>
}

interface ContentSegment : UniqueEntity

interface ContentSegments {
    val segments: Collection<ContentSegment>
}

interface TextContentSegment : ContentSegment {
    val highlightedText: HighlightedText
}

interface ImageContentSegment : ContentSegment {
    val image: Image
    val description: HighlightedText?
}

interface ListContentSegment : ContentSegment {
    val content: Collection<ContentSegments>
    val header: HighlightedText
}

interface TableContentSegment : ContentSegment {
    val header: HighlightedText
    val rows: Collection<TableRow>
}

interface TableRow {
    val cells: Collection<TableCell>
}

interface TableCell {
    val contentSegments: ContentSegments
}