package com.intelligentcomments.core.domain.rd

import com.intelligentcomments.core.comments.codeHighlighting.CodeFragmentHighlightingHost
import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.ContentProcessingStrategyImpl
import com.intelligentcomments.core.domain.impl.HighlightedTextImpl
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.string.printToString

open class ContentSegmentFromRd(
  private val contentSegment: RdContentSegment,
  override val parent: Parentable?,
) : UniqueEntityImpl(), ContentSegment {
  companion object {
    fun getFrom(contentSegment: RdContentSegment, parent: Parentable?, project: Project): ContentSegmentFromRd {
      return when (contentSegment) {
        is RdTextSegment -> TextContentSegmentFromRd(contentSegment, parent, project)
        is RdListSegment -> ListSegmentFromRd(contentSegment, parent, project)
        is RdImageSegment -> FileBasedImageSegmentFromRd(contentSegment, parent, project)
        is RdTableSegment -> TableSegmentFromRd(contentSegment, parent, project)
        is RdParagraphSegment -> ParagraphContentSegmentFromRd(contentSegment, parent, project)
        is RdTypeParam -> TypeParamFromRd(contentSegment, parent, project)
        is RdParam -> ParameterFromRd(contentSegment, parent, project)
        is RdReturnSegment -> ReturnFromRd(contentSegment, parent, project)
        is RdRemarksSegment -> RemarksSegmentFromRd(contentSegment, parent, project)
        is RdExceptionsSegment -> ExceptionSegmentFromRd(contentSegment, parent, project)
        is RdSeeAlsoContentSegment -> SeeAlsoSegmentFromRd.getFor(contentSegment, parent, project)
        is RdExampleSegment -> ExampleFromRd(contentSegment, parent, project)
        is RdSummarySegment -> SummaryContentSegmentFromRd(contentSegment, parent, project)
        is RdCodeContentSegment -> CodeSegmentFromRd(contentSegment, parent, project)
        is RdValueSegment -> ValueContentSegmentFromRd(contentSegment, parent, project)
        is RdTextInvariant -> TextInvariantFromRdSegment(contentSegment, parent, project)
        is RdToDoContentSegment -> ToDoContentSegmentFromRd(contentSegment, parent, project)
        is RdReferenceContentSegment -> ReferenceContentSegmentFromRd(contentSegment, parent, project)
        is RdTicketContentSegment -> TicketSegmentFromRd(contentSegment, parent, project)
        is RdHackContentSegment -> HackContentSegmentFromRd(contentSegment, parent, project)
        is RdInlineContentSegment -> InlineContentSegmentFromRd(contentSegment, parent, project)
        is RdDefaultSegmentWithContent -> EntityWithContentSegmentsFromRd(contentSegment, parent, project)
        else -> throw IllegalArgumentException(contentSegment.toString())
      }
    }
  }
}

open class EntityWithContentSegmentsFromRd(
  entity: RdSegmentWithContent,
  override val parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(entity, parent), EntityWithContentSegments {
  override val content: ContentSegments = ContentSegmentsFromRd(entity.content, this, project)
}

class ContentSegmentsFromRd(
  contentSegments: RdContentSegments,
  override val parent: Parentable?,
  project: Project
) : ContentSegments {
  private val myCachedSegments: MutableList<ContentSegment> = contentSegments.content.mapNotNull {
    val segment = ContentSegmentFromRd.getFrom(it, this, project)
    val showEmptyContent = RiderIntelligentCommentsSettingsProvider.getInstance().showEmptyContent.value
    if (segment is EntityWithContentSegments && segment.content.segments.isEmpty() && !showEmptyContent) {
      return@mapNotNull null
    }

    return@mapNotNull segment
  }.toMutableList()

  override val segments
    get() = myCachedSegments

  override fun processSegments(strategy: ContentProcessingStrategy) {
    strategy.process(myCachedSegments)
  }
}

class ParagraphContentSegmentFromRd(
  paragraph: RdParagraphSegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(paragraph, parent, project), ParagraphContentSegment

class SummaryContentSegmentFromRd(
  rdSummary: RdSummarySegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdSummary, parent, project), SummaryContentSegment

class ValueContentSegmentFromRd(
  segment: RdValueSegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(segment, parent, project), ValueSegment

class TextContentSegmentFromRd(
  segment: RdTextSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(segment, parent), TextContentSegment {
  override val highlightedText: HighlightedText = segment.text.toIdeaHighlightedText(project, this)
}

fun RdHighlightedText.toIdeaHighlightedText(project: Project, parent: Parentable?): HighlightedText {
  return HighlightedTextImpl(
    text,
    parent,
    highlighters?.map { it.toIdeaHighlighter(project, null) } ?: listOf()
  )
}

class ListSegmentFromRd(
  segment: RdListSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(segment, parent), ListContentSegment {
  override val content: Collection<ListItem> = segment.listContent.map {
    val header = if (it.header == null) null else ContentSegmentsFromRd(it.header, this, project)
    val description = if (it.description == null) null else ContentSegmentsFromRd(it.description, this, project)
    ListItem(header, description)
  }

  override val listKind: ListSegmentKind = when (segment.listKind) {
    RdListKind.Number -> ListSegmentKind.Number
    RdListKind.Bullet -> ListSegmentKind.Bullet
    else -> throw IllegalArgumentException(segment.listKind.toString())
  }

  override val header: HighlightedText? = segment.header?.toIdeaHighlightedText(project, this)
}

class FileBasedImageSegmentFromRd(
  segment: RdImageSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(segment, parent), ImageContentSegment {
  override val description: HighlightedText = segment.description.toIdeaHighlightedText(project, this)
  override val sourceReference: Reference = ReferenceFromRd.getFrom(project, segment.sourceReference)


  override fun isValid(): Boolean {
    sourceReference as FileReference
    val file = sourceReference.file ?: return false
    return file.exists()
  }
}

class TableSegmentFromRd(
  segment: RdTableSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(segment, parent), TableContentSegment {
  private val cachedRows = segment.rows.map { TableRowFromRd(it, this, project) }.toMutableList()

  override val header: HighlightedText? = segment.header?.toIdeaHighlightedText(project, this)
  override val rows: Collection<TableRow>
    get() = cachedRows.toList()

  override fun removeEmptyRowsAndCols() {
    for (i in cachedRows.indices.reversed()) {
      if (cachedRows[i].cells.all { it.contentSegments.segments.isEmpty() }) {
        cachedRows.removeAt(i)
      }
    }

    if (cachedRows.size == 0) return

    val colsToRemove = mutableListOf<Int>()
    for (j in cachedRows[0].cells.indices) {
      var isEmpty = true
      for (i in cachedRows.indices) {
        if (!cachedRows[i].cells[j].contentSegments.segments.isEmpty()) {
          isEmpty = false
          break
        }
      }

      if (isEmpty) {
        colsToRemove.add(j)
      }
    }

    for (idx in colsToRemove.reversed()) {
      for (row in cachedRows) {
        row.removeCelAt(idx)
      }
    }
  }
}

class TableRowFromRd(
  row: RdTableRow,
  override val parent: Parentable?,
  project: Project
) : TableRow {
  private val cachedCells = row.cells.map { TableCellFromRd(it, this, project) }.toMutableList()
  override val cells: List<TableCell>
    get() = cachedCells

  fun removeCelAt(idx: Int) {
    cachedCells.removeAt(idx)
  }
}

class TableCellFromRd(
  cell: RdTableCell,
  override val parent: Parentable?,
  project: Project
) : TableCell {
  override val contentSegments: ContentSegments = ContentSegmentsFromRd(cell.content, this, project)
  override val properties: TableCellProperties

  init {
    properties = if (cell.properties != null) {
      TableCellPropertiesFromRd(cell.properties)
    } else {
      TableCellPropertiesFromRd.defaultProperties
    }
  }
}

class TableCellPropertiesFromRd(properties: RdTableCellProperties) : TableCellProperties {
  companion object {
    val defaultProperties =
      TableCellPropertiesFromRd(RdTableCellProperties(RdHorizontalAlignment.Center, RdVerticalAlignment.Center, false))
  }

  override val verticalAlignment: VerticalAlignment = properties.verticalAlignment.toAlignment()
  override val horizontalAlignment: HorizontalAlignment = properties.horizontalAlignment.toAlignment()
  override val isHeader: Boolean = properties.isHeader
}

fun RdVerticalAlignment.toAlignment(): VerticalAlignment = when (this) {
  RdVerticalAlignment.Top -> VerticalAlignment.TOP
  RdVerticalAlignment.Bottom -> VerticalAlignment.BOTTOM
  RdVerticalAlignment.Center -> VerticalAlignment.CENTER
}

fun RdHorizontalAlignment.toAlignment(): HorizontalAlignment = when (this) {
  RdHorizontalAlignment.Center -> HorizontalAlignment.CENTER
  RdHorizontalAlignment.Right -> HorizontalAlignment.RIGHT
  RdHorizontalAlignment.Left -> HorizontalAlignment.LEFT
}

class ParameterFromRd(
  rdParam: RdParam,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdParam, parent, project), ParameterSegment {
  override val name = rdParam.name.toIdeaHighlightedText(project, this)
}

class TypeParamFromRd(
  rdTypeParam: RdTypeParam,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdTypeParam, parent, project), TypeParamSegment {
  override val name = rdTypeParam.name.toIdeaHighlightedText(project, this)
}

class ReturnFromRd(
  rdReturn: RdReturnSegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdReturn, parent, project), ReturnSegment

class ExampleFromRd(
  rdExample: RdExampleSegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdExample, parent, project), ExampleContentSegment

class RemarksSegmentFromRd(
  rdRemarksSection: RdRemarksSegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdRemarksSection, parent, project), RemarksSegment

class ExceptionSegmentFromRd(
  rdExceptionSegment: RdExceptionsSegment,
  parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdExceptionSegment, parent, project), ExceptionSegment {
  override val name: HighlightedText = rdExceptionSegment.name.toIdeaHighlightedText(project, this)
}

open class SeeAlsoSegmentFromRd(
  rdSeeAlso: RdSeeAlsoContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(rdSeeAlso, parent), SeeAlsoSegment {
  companion object {
    fun getFor(rdSeeAlso: RdSeeAlsoContentSegment, parent: Parentable?, project: Project): SeeAlsoSegmentFromRd {
      return when (rdSeeAlso) {
        is RdSeeAlsoLinkContentSegment -> SeeAlsoLinkSegmentFromRd(rdSeeAlso, parent, project)
        is RdSeeAlsoMemberContentSegment -> SeeAlsoMemberSegmentFromRd(rdSeeAlso, parent, project)
        else -> throw IllegalArgumentException(rdSeeAlso.printToString())
      }
    }
  }

  override val description: HighlightedText = rdSeeAlso.description.toIdeaHighlightedText(project, this)
}

class SeeAlsoLinkSegmentFromRd(
  rdSeeAlsoLink: RdSeeAlsoLinkContentSegment,
  parent: Parentable?,
  project: Project
) : SeeAlsoSegmentFromRd(rdSeeAlsoLink, parent, project), SeeAlsoLinkSegment {
  override val reference: ExternalReference
    get() = TODO("Not yet implemented")
}

class SeeAlsoMemberSegmentFromRd(
  rdSeeAlsoLink: RdSeeAlsoMemberContentSegment,
  parent: Parentable?,
  project: Project
) : SeeAlsoSegmentFromRd(rdSeeAlsoLink, parent, project), SeeAlsoMemberSegment {
  override val reference: CodeEntityReference
    get() = TODO("Not yet implemented")
}

class CodeSegmentFromRd(
  rdCodeSegment: RdCodeContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(rdCodeSegment, parent), CodeSegment {
  companion object {
    private val logger = getLogger<CodeSegmentFromRd>()
  }

  private val highlightingHost = project.service<CodeFragmentHighlightingHost>()

  override val code = Property(rdCodeSegment.code.toIdeaHighlightedText(project, this))

  init {
    highlightingHost.requestFullHighlighting(this, rdCodeSegment.highlightingRequestId) {
      val previousCodeText = code.value.text
      if (it.text == previousCodeText) {
        code.set(it)
      } else {
        logger.logAssertion("Expected the code text to be the same: $previousCodeText VS ${it.text}")
      }
    }
  }
}

class InlineContentSegmentFromRd(
  rdSegment: RdInlineContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(rdSegment, parent), InlineContentSegment {
  override val name: HighlightedText? = rdSegment.name?.toIdeaHighlightedText(project, this)
  override val text: HighlightedText = rdSegment.text.toIdeaHighlightedText(project, this)
  override val nameKind: NameKind = rdSegment.nameKind.toIdeaNameKind()
}

class TicketSegmentFromRd(
  rdSegment: RdTicketContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(rdSegment, parent), TicketContentSegment {
  override val reference: Reference = ReferenceFromRd.getFrom(project, rdSegment.source)
  override val description: EntityWithContentSegments = EntityWithContentSegmentsFromRd(rdSegment.content, this, project)
}

class HackContentSegmentFromRd(
  rdSegment: RdHackContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(rdSegment, parent), HackWithTicketsContentSegment {
  override val name: HighlightedText? = rdSegment.name?.toIdeaHighlightedText(project, this)
  override val content: EntityWithContentSegments = EntityWithContentSegmentsFromRd(rdSegment.description, this, project)

  init {
    content.content.processSegments(project.service<ContentProcessingStrategyImpl>())
  }
}

class ToDoContentSegmentFromRd(
  contentSegment: RdToDoContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(contentSegment, parent), ToDoWithTicketsContentSegment {
  override val name: HighlightedText? = contentSegment.name?.toIdeaHighlightedText(project, this)
  override val content: EntityWithContentSegments = EntityWithContentSegmentsFromRd(contentSegment.content, this, project)

  init {
    content.content.processSegments(project.service<ContentProcessingStrategyImpl>())
  }
}