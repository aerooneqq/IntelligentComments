package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

open class ContentSegmentFromRd(private val contentSegment: RdContentSegment) : UniqueEntityImpl(), ContentSegment {
    companion object {
        fun getFrom(contentSegment: RdContentSegment, project: Project): ContentSegmentFromRd {
            return when(contentSegment) {
                is RdTextSegment -> TextContentSegmentFromRd(contentSegment, project)
                is RdListSegment -> ListSegmentFromRd(contentSegment, project)
                is RdFileBasedImageSegment -> FileBasedImageSegmentFromRd(contentSegment, project)
                is RdTableSegment -> TableSegmentFromRd(contentSegment, project)
                else -> throw IllegalArgumentException(contentSegment.toString())
            }
        }
    }
}

class ContentSegmentsFromRd(contentSegments: RdContentSegments,
                            project: Project) : ContentSegments {
    override val segments: Collection<ContentSegment> = contentSegments.content.map { ContentSegmentFromRd.getFrom(it, project) }
}

class TextContentSegmentFromRd(segment: RdTextSegment,
                               project: Project) : ContentSegmentFromRd(segment), TextContentSegment {
    override val highlightedText: HighlightedText = HighlightedTextFromRd(segment.text, project)
}

class HighlightedTextFromRd(highlightedText: RdHighlightedText,
                            project: Project) : HighlightedText {
    override val highlighters: Collection<TextHighlighter>
    override val text: String = highlightedText.text

    init {
        highlighters = highlightedText.highlighters?.map { TextHighlighterFromRd(project, it) } ?: listOf()
    }
}

class ListSegmentFromRd(segment: RdListSegment, project: Project) : ContentSegmentFromRd(segment), ListContentSegment {
    override val content: Collection<ContentSegments> = segment.listContent.map { ContentSegmentsFromRd(it, project) }
    override val header: HighlightedText = HighlightedTextFromRd(segment.header, project)
}

class FileBasedImageSegmentFromRd(private val segment: RdFileBasedImageSegment,
                                  project: Project) : ContentSegmentFromRd(segment), ImageContentSegment {
    override val description: HighlightedText = HighlightedTextFromRd(segment.description, project)

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

class TableSegmentFromRd(private val segment: RdTableSegment,
                         project: Project) : ContentSegmentFromRd(segment), TableContentSegment {
    override val header: HighlightedText = HighlightedTextFromRd(segment.header, project)
    override val rows: Collection<TableRow> = segment.rows.map { TableRowFromRd(it, project) }
}

class TableRowFromRd(private val row: RdTableRow, project: Project): TableRow {
    override val cells: Collection<TableCell> = row.cells.map { TableCellFromRd(it, project) }
}

class TableCellFromRd(cell: RdTableCell, project: Project) : UiInteractionModelBase(project), TableCell {
    override val contentSegments: ContentSegments = ContentSegmentsFromRd(cell.content, project)
}