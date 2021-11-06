package com.intelligentComments.core.domain.core

import java.awt.Image

interface IntelligentCommentContent : UniqueEntity {
    val segments: Collection<ContentSegment>
}

interface ContentSegment : UniqueEntity

interface ContentSegments {
    val segments: Collection<ContentSegment>
}

interface ParagraphContentSegment : ContentSegment {
    val content: ContentSegments
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
    val properties: TableCellProperties
}

enum class HorizontalAlignment {
    CENTER,
    LEFT,
    RIGHT
}

enum class VerticalAlignment {
    CENTER,
    TOP,
    BOTTOM
}

interface TableCellProperties {
    val verticalAlignment: VerticalAlignment
    val horizontalAlignment: HorizontalAlignment
    val isHeader: Boolean
}

interface Parameter : ContentSegment {
    val name: String
    val description: ContentSegments
}