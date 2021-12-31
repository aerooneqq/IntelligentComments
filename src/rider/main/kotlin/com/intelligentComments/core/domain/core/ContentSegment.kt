package com.intelligentComments.core.domain.core

import java.awt.Image

interface IntelligentCommentContent : UniqueEntity {
  val segments: Collection<ContentSegment>

  fun processSegments(strategy: ContentProcessingStrategy)
}

interface ContentProcessingStrategy {
  fun process(segments: MutableList<ContentSegment>)
}

interface ContentSegment : UniqueEntity

interface ContentSegments {
  val segments: Collection<ContentSegment>
}

interface EntityWithContentSegments : ContentSegment {
  val content: ContentSegments
}

interface ParagraphContentSegment : EntityWithContentSegments

interface SummaryContentSegment : EntityWithContentSegments

interface ExampleContentSegment : EntityWithContentSegments

interface TextContentSegment : ContentSegment {
  val highlightedText: HighlightedText
}

interface ImageContentSegment : ContentSegment {
  val image: Image
  val description: HighlightedText?
}

enum class ListSegmentKind {
  Bullet,
  Number
}

interface ListContentSegment : ContentSegment {
  val listKind: ListSegmentKind
  val content: Collection<ListItem>
  val header: HighlightedText?
}

data class ListItem(val header: ContentSegments?, val description: ContentSegments?)

interface TableContentSegment : ContentSegment {
  val header: HighlightedText?
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

interface ArbitraryParamSegment : EntityWithContentSegments {
  val name: String
}

interface ParameterSegment : ArbitraryParamSegment
interface TypeParamSegment : ArbitraryParamSegment


interface ReturnSegment : EntityWithContentSegments

interface RemarksSegment : EntityWithContentSegments

interface ExceptionSegment : EntityWithContentSegments {
  val name: String
}

interface SeeAlsoSegment : ContentSegment {
  val description: HighlightedText
}

interface SeeAlsoLinkSegment : SeeAlsoSegment {
  val reference: ExternalReference
}

interface SeeAlsoMemberSegment : SeeAlsoSegment {
  val reference: CodeEntityReference
}

interface GroupedContentSegment<out T : ContentSegment> : ContentSegment {
  val segments: List<T>
}