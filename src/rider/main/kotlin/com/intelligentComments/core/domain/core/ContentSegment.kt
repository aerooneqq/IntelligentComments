package com.intelligentComments.core.domain.core

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.code.CodeSegmentUiModel
import com.intelligentComments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel
import com.intelligentComments.ui.comments.model.content.hacks.HackTextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentComments.ui.comments.model.content.params.ParameterUiModel
import com.intelligentComments.ui.comments.model.content.params.TypeParamUiModel
import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.summary.SummaryUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.todo.ToDoTextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.value.ValueUiModel
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.reactive.Property

interface IntelligentCommentContent : EntityWithContentSegments

interface ContentProcessingStrategy {
  fun process(segments: MutableList<ContentSegment>)
}

interface Parentable {
  val parent: Parentable?
}

class NotSupportedUiModelCreationError(typeName: String) : Exception(typeName)

interface ContentSegment : Parentable, UniqueEntity {
  fun isValid() = true
  fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    throw NotSupportedUiModelCreationError(javaClass.name)
  }
}

interface ContentSegments : Parentable {
  val segments: Collection<ContentSegment>

  fun processSegments(strategy: ContentProcessingStrategy)
}

interface EntityWithContentSegments : ContentSegment {
  val content: ContentSegments

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ContentSegmentsUiModel(project, parent, content)
  }
}

interface ValueSegment : EntityWithContentSegments {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ValueUiModel(project, parent, this)
  }
}

interface ParagraphContentSegment : EntityWithContentSegments {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ParagraphUiModel(project, parent, this)
  }
}

interface SummaryContentSegment : EntityWithContentSegments {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return SummaryUiModel(project, parent, this)
  }
}

interface ExampleContentSegment : EntityWithContentSegments {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ExampleSegmentUiModel(project, parent, this)
  }
}

interface TextContentSegment : ContentSegment {
  val highlightedText: HighlightedText

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return TextContentSegmentUiModel(project, parent, this)
  }
}

interface ImageContentSegment : ContentSegment {
  val sourceReference: Reference
  val description: HighlightedText?

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ImageContentSegmentUiModel(project, parent, this)
  }
}

enum class ListSegmentKind {
  Bullet,
  Number
}

interface ListContentSegment : ContentSegment {
  val listKind: ListSegmentKind
  val content: Collection<ListItem>
  val header: HighlightedText?

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ListContentSegmentUiModel(project, parent, this)
  }
}

data class ListItem(val header: ContentSegments?, val description: ContentSegments?)

interface TableContentSegment : ContentSegment {
  val header: HighlightedText?
  val rows: Collection<TableRow>

  fun removeEmptyRowsAndCols()

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return TableContentSegmentUiModel(project, parent, this)
  }
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

interface ParameterSegment : ArbitraryParamSegment {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ParameterUiModel(project, parent, this)
  }
}
interface TypeParamSegment : ArbitraryParamSegment {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return TypeParamUiModel(project, parent, this)
  }
}


interface ReturnSegment : EntityWithContentSegments {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ReturnUiModel(project, parent, this)
  }
}

interface RemarksSegment : EntityWithContentSegments {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return RemarksUiModel(project, parent, this)
  }
}

interface ExceptionSegment : EntityWithContentSegments {
  val name: HighlightedText

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ExceptionUiModel(project, parent, this)
  }
}

interface SeeAlsoSegment : ContentSegment {
  val description: HighlightedText

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return SeeAlsoUiModel.getFor(project, parent, this)
  }
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

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return CodeSegmentUiModel(project, parent, this)
  }
}

interface ToDoTextContentSegment : ContentSegment {
  val text: HighlightedText

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return ToDoTextContentSegmentUiModel(project, parent, this)
  }
}

interface HackTextContentSegment : ContentSegment {
  val text: HighlightedText

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return HackTextContentSegmentUiModel(project, parent, this)
  }
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

fun removeContentSegmentsRecursively(
  segments: MutableList<ContentSegment>,
  criteria: (ContentSegment) -> Boolean
) {
  for (index in segments.indices.reversed()) {
    if (criteria(segments[index])) {
      segments.removeAt(index)
    }
  }

  for (segment in segments) {
    if (segment is EntityWithContentSegments) {
      val innerSegments = segment.content.segments as? MutableList<ContentSegment> ?: continue
      removeContentSegmentsRecursively(innerSegments, criteria)
    }
  }
}