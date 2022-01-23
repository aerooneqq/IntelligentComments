package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.codeHighlighting.CodeFragmentHighlightingHost
import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.string.printToString
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

open class ContentSegmentFromRd(
  private val contentSegment: RdContentSegment,
  override val parent: Parentable?,
) : UniqueEntityImpl(), ContentSegment {
  companion object {
    fun getFrom(contentSegment: RdContentSegment, parent: Parentable?, project: Project): ContentSegmentFromRd {
      return when (contentSegment) {
        is RdTextSegment -> TextContentSegmentFromRd(contentSegment, parent, project)
        is RdListSegment -> ListSegmentFromRd(contentSegment, parent, project)
        is RdFileBasedImageSegment -> FileBasedImageSegmentFromRd(contentSegment, parent, project)
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
    ListKind.Number -> ListSegmentKind.Number
    ListKind.Bullet -> ListSegmentKind.Bullet
    else -> throw IllegalArgumentException(segment.listKind.toString())
  }

  override val header: HighlightedText? = segment.header?.toIdeaHighlightedText(project, this)
}

class FileBasedImageSegmentFromRd(
  private val segment: RdFileBasedImageSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(segment, parent), ImageContentSegment {
  override val description: HighlightedText = segment.description.toIdeaHighlightedText(project, this)

  private var cachedImage: Image? = null
  override val image: Image
    get() {
      val loadedImage = cachedImage
      if (loadedImage == null) {
        val image = ImageIO.read(File(segment.path))
        cachedImage = image
        return image
      }

      return loadedImage
    }
}

class TableSegmentFromRd(
  segment: RdTableSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(segment, parent), TableContentSegment {
  override val header: HighlightedText? = segment.header?.toIdeaHighlightedText(project, this)
  override val rows: Collection<TableRow> = segment.rows.map { TableRowFromRd(it, this, project) }
}

class TableRowFromRd(
  row: RdTableRow,
  override val parent: Parentable?,
  project: Project
) : TableRow {
  override val cells: Collection<TableCell> = row.cells.map { TableCellFromRd(it, this, project) }
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