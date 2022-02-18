package com.intelligentComments.core.domain.core

import com.jetbrains.rd.util.reactive.Property

interface IntelligentCommentContent : EntityWithContentSegments

interface ContentProcessingStrategy {
  fun process(segments: MutableList<ContentSegment>)
}

interface Parentable {
  val parent: Parentable?
}

interface ContentSegment : Parentable, UniqueEntity

interface ContentSegments : Parentable {
  val segments: Collection<ContentSegment>

  fun processSegments(strategy: ContentProcessingStrategy)
}

interface EntityWithContentSegments : ContentSegment {
  val content: ContentSegments
}

interface ValueSegment : EntityWithContentSegments

interface ParagraphContentSegment : EntityWithContentSegments

interface SummaryContentSegment : EntityWithContentSegments

interface ExampleContentSegment : EntityWithContentSegments

interface TextContentSegment : ContentSegment {
  val highlightedText: HighlightedText
}

interface ImageContentSegment : ContentSegment {
  val sourceReference: Reference
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

  fun removeEmptyRowsAndCols()
}

interface TableRow : Parentable {
  val cells: Collection<TableCell>
}

interface TableCell : Parentable {
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
  val name: HighlightedText
}

interface ParameterSegment : ArbitraryParamSegment
interface TypeParamSegment : ArbitraryParamSegment


interface ReturnSegment : EntityWithContentSegments

interface RemarksSegment : EntityWithContentSegments

interface ExceptionSegment : EntityWithContentSegments {
  val name: HighlightedText
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

interface CodeSegment : ContentSegment {
  val code: Property<HighlightedText>
}

fun tryFindComment(parentable: Parentable): CommentBase? {
  var current: Parentable? = parentable
  while (current != null &&  current !is CommentBase) {
    current = current.parent
  }

  return current as? CommentBase
}

fun visitAllContentSegments(startSegments: Collection<ContentSegment>, action: (ContentSegment) -> Unit) {
  for (segment in startSegments) {
    if (segment is EntityWithContentSegments) {
      visitAllContentSegments(segment.content.segments, action)
    }

    action(segment)
  }
}