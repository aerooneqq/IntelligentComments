package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.util.string.printToString
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

open class ContentSegmentFromRd(private val contentSegment: RdContentSegment) : UniqueEntityImpl(), ContentSegment {
  companion object {
    fun getFrom(contentSegment: RdContentSegment, project: Project): ContentSegmentFromRd {
      return when (contentSegment) {
        is RdTextSegment -> TextContentSegmentFromRd(contentSegment, project)
        is RdListSegment -> ListSegmentFromRd(contentSegment, project)
        is RdFileBasedImageSegment -> FileBasedImageSegmentFromRd(contentSegment, project)
        is RdTableSegment -> TableSegmentFromRd(contentSegment, project)
        is RdParagraphSegment -> ParagraphContentSegmentFromRd(contentSegment, project)
        is RdTypeParam -> TypeParamFromRd(contentSegment, project)
        is RdParam -> ParameterFromRd(contentSegment, project)
        is RdReturnSegment -> ReturnFromRd(contentSegment, project)
        is RdRemarksSegment -> RemarksSegmentFromRd(contentSegment, project)
        is RdExceptionsSegment -> ExceptionSegmentFromRd(contentSegment, project)
        is RdSeeAlsoContentSegment -> SeeAlsoSegmentFromRd.getFor(contentSegment, project)
        is RdExampleSegment -> ExampleFromRd(contentSegment, project)
        is RdSummarySegment -> SummaryContentSegmentFromRd(contentSegment, project)
        else -> throw IllegalArgumentException(contentSegment.toString())
      }
    }
  }
}

class ContentSegmentsFromRd(
  contentSegments: RdContentSegments,
  project: Project
) : ContentSegments {
  override val segments = contentSegments.content.map { ContentSegmentFromRd.getFrom(it, project) }
}

class ParagraphContentSegmentFromRd(
  paragraph: RdParagraphSegment,
  project: Project
) : ContentSegmentFromRd(paragraph), ParagraphContentSegment {
  override val content: ContentSegments = ContentSegmentsFromRd(paragraph.content, project)
}

class SummaryContentSegmentFromRd(
  rdSummary: RdSummarySegment,
  project: Project
) : ContentSegmentFromRd(rdSummary), SummaryContentSegment {
  override val content: ContentSegments = ContentSegmentsFromRd(rdSummary.content, project)
}

class TextContentSegmentFromRd(
  segment: RdTextSegment,
  project: Project
) : ContentSegmentFromRd(segment), TextContentSegment {
  override val highlightedText: HighlightedText = segment.text.toIdeaHighlightedText(project)
}


fun RdHighlightedText.toIdeaHighlightedText(project: Project): HighlightedText {
  return HighlightedTextImpl(
    text,
    highlighters?.map { it.toIdeaHighlighter(project) } ?: listOf()
  )
}

class ListSegmentFromRd(segment: RdListSegment, project: Project) : ContentSegmentFromRd(segment), ListContentSegment {
  override val content: Collection<ListItem> = segment.listContent.map {
    val header = if (it.header == null) null else ContentSegmentsFromRd(it.header, project)
    val description = if (it.description == null) null else ContentSegmentsFromRd(it.description, project)
    ListItem(header, description)
  }

  override val listKind: ListSegmentKind = when (segment.listKind) {
    ListKind.Number -> ListSegmentKind.Number
    ListKind.Bullet -> ListSegmentKind.Bullet
    else -> throw IllegalArgumentException(segment.listKind.toString())
  }

  override val header: HighlightedText? = segment.header?.toIdeaHighlightedText(project)
}

class FileBasedImageSegmentFromRd(
  private val segment: RdFileBasedImageSegment,
  project: Project
) : ContentSegmentFromRd(segment), ImageContentSegment {
  override val description: HighlightedText = segment.description.toIdeaHighlightedText(project)

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
  project: Project
) : ContentSegmentFromRd(segment), TableContentSegment {
  override val header: HighlightedText? = segment.header?.toIdeaHighlightedText(project)
  override val rows: Collection<TableRow> = segment.rows.map { TableRowFromRd(it, project) }
}

class TableRowFromRd(row: RdTableRow, project: Project) : TableRow {
  override val cells: Collection<TableCell> = row.cells.map { TableCellFromRd(it, project) }
}

class TableCellFromRd(cell: RdTableCell, project: Project) : UiInteractionModelBase(project), TableCell {
  override val contentSegments: ContentSegments = ContentSegmentsFromRd(cell.content, project)
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

class ParameterFromRd(rdParam: RdParam, project: Project) : ContentSegmentFromRd(rdParam), ParameterSegment {
  override val name: String = rdParam.name
  override val content: ContentSegments = ContentSegmentsFromRd(rdParam.content, project)
}

class TypeParamFromRd(rdTypeParam: RdTypeParam, project: Project) : ContentSegmentFromRd(rdTypeParam), TypeParamSegment {
  override val name: String = rdTypeParam.name
  override val content: ContentSegments = ContentSegmentsFromRd(rdTypeParam.content, project)
}

class ReturnFromRd(rdReturn: RdReturnSegment, project: Project) : ContentSegmentFromRd(rdReturn), ReturnSegment {
  override val content: ContentSegments = ContentSegmentsFromRd(rdReturn.content, project)
}

class ExampleFromRd(rdExample: RdExampleSegment, project: Project) : ContentSegmentFromRd(rdExample), ExampleContentSegment {
  override val content: ContentSegments = ContentSegmentsFromRd(rdExample.content, project)
}

class RemarksSegmentFromRd(
  rdRemarksSection: RdRemarksSegment,
  project: Project
) : ContentSegmentFromRd(rdRemarksSection), RemarksSegment {
  override val content: ContentSegments = ContentSegmentsFromRd(rdRemarksSection.content, project)
}

class ExceptionSegmentFromRd(
  rdExceptionSegment: RdExceptionsSegment,
  project: Project
) : ContentSegmentFromRd(rdExceptionSegment), ExceptionSegment {
  override val name: String = rdExceptionSegment.name
  override val content: ContentSegments = ContentSegmentsFromRd(rdExceptionSegment.content, project)
}

open class SeeAlsoSegmentFromRd(
  rdSeeAlso: RdSeeAlsoContentSegment,
  project: Project
) : ContentSegmentFromRd(rdSeeAlso), SeeAlsoSegment {
  companion object {
    fun getFor(rdSeeAlso: RdSeeAlsoContentSegment, project: Project): SeeAlsoSegmentFromRd {
      return when(rdSeeAlso) {
        is RdSeeAlsoLinkContentSegment -> SeeAlsoLinkSegmentFromRd(rdSeeAlso, project)
        is RdSeeAlsoMemberContentSegment -> SeeAlsoMemberSegmentFromRd(rdSeeAlso, project)
        else -> throw IllegalArgumentException(rdSeeAlso.printToString())
      }
    }
  }

  override val description: HighlightedText = rdSeeAlso.description.toIdeaHighlightedText(project)
}

class SeeAlsoLinkSegmentFromRd(
  rdSeeAlsoLink: RdSeeAlsoLinkContentSegment,
  project: Project
) : SeeAlsoSegmentFromRd(rdSeeAlsoLink, project), SeeAlsoLinkSegment {
  override val reference: ExternalReference
    get() = TODO("Not yet implemented")
}

class SeeAlsoMemberSegmentFromRd(
  rdSeeAlsoLink: RdSeeAlsoMemberContentSegment,
  project: Project
) : SeeAlsoSegmentFromRd(rdSeeAlsoLink, project), SeeAlsoMemberSegment {
  override val reference: CodeEntityReference
    get() = TODO("Not yet implemented")
}