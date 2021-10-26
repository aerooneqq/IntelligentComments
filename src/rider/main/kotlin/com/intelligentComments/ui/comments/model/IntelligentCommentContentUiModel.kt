package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import java.awt.Image
import java.awt.image.ImageObserver
import java.lang.Integer.max
import java.lang.Integer.min

class IntelligentCommentContentUiModel(project: Project,
                                       content: IntelligentCommentContent) : UiInteractionModelBase(project) {
    private val mySegments = mutableListOf<ContentSegmentUiModel>()

    val segments: Collection<ContentSegmentUiModel> = mySegments

    init {
        for (segment in content.segments) mySegments.add(ContentSegmentUiModel.getFrom(project, segment))
    }
}

open class ContentSegmentUiModel(project: Project,
                                 segment: ContentSegment) : UiInteractionModelBase(project) {
    companion object {
        fun getFrom(project: Project, segment: ContentSegment): ContentSegmentUiModel {
            return when(segment) {
                is TextContentSegment -> TextContentSegmentUiModel(project, segment)
                is ListContentSegment -> ListContentSegmentUiModel(project, segment)
                is ImageContentSegment -> ImageContentSegmentUiModel(project, segment)
                is TableContentSegment -> TableContentSegmentUiModel(project, segment)
                else -> throw IllegalArgumentException(segment.toString())
            }
        }
    }
}

class ContentSegmentsUiModel(project: Project, content: ContentSegments): UiInteractionModelBase(project) {
    val content: Collection<ContentSegmentUiModel> = content.segments.map { ContentSegmentUiModel.getFrom(project, it) }
}

class TextContentSegmentUiModel(project: Project,
                                textSegment: TextContentSegment) : ContentSegmentUiModel(project, textSegment) {
    private val highlightedTextWrapper = HighlightedTextUiWrapper(project, textSegment.highlightedText)
    val text = highlightedTextWrapper.text
    val highlighters = highlightedTextWrapper.highlighters
}

class HighlightedTextUiWrapper(project: Project, highlightedText: HighlightedText) {
    val text = highlightedText.text
    val highlighters = highlightedText.highlighters.map { HighlighterUiModel.getFor(project, it) }
}

class ListContentSegmentUiModel(project: Project,
                                listSegment: ListContentSegment) : ContentSegmentUiModel(project, listSegment), ExpandableUiModel {
    val header = ListContentSegmentHeader(project, listSegment.header, this)
    val contentSegments = listSegment.content.map { ContentSegmentsUiModel(project, it) }

    override var isExpanded = true
}

class ListContentSegmentHeader(project: Project,
                               highlightedText: HighlightedText,
                               private val parent: ListContentSegmentUiModel) : UiInteractionModelBase(project) {
    val textWrapper = HighlightedTextUiWrapper(project, highlightedText)

    override fun handleClick(e: EditorMouseEvent): Boolean {
        parent.isExpanded = !parent.isExpanded
        return true
    }
}

class ImageContentSegmentUiModel(project: Project,
                                 imageSegment: ImageContentSegment) : ContentSegmentUiModel(project, imageSegment) {
    val description: HighlightedTextUiWrapper?
    val imageHolder = ImageHolder(imageSegment)

    init {
        val description = imageSegment.description
        this.description = if (description != null) {
            HighlightedTextUiWrapper(project, description)
        } else {
            null
        }
    }
}

class DummyImageObserver : ImageObserver {
    companion object {
        val instance = DummyImageObserver()
    }

    override fun imageUpdate(img: Image?, infoflags: Int, x: Int, y: Int, width: Int, height: Int): Boolean = false
}

class ImageHolder(private val imageContentSegment: ImageContentSegment) {
    companion object {
        val maxDimension = 500
    }

    var width = -1
        get() {
            initializeIfNeeded()
            return field
        }
        private set

    var height = -1
        get() {
            initializeIfNeeded()
            return field
        }
        private set

    val image: Image
        get() = imageContentSegment.image


    private var isInitialized = false

    private fun initializeIfNeeded() {
        if (isInitialized) return

        var imgWidth = image.getWidth(DummyImageObserver.instance)
        var imgHeight = image.getHeight(DummyImageObserver.instance)

        val maxImgDimension = max(imgWidth, imgHeight)
        val minImgDimension = min(imgWidth, imgHeight)
        val scaleCoeff = minImgDimension.toDouble() / maxImgDimension
        if (maxImgDimension > maxDimension) {
            if (imgWidth == maxImgDimension) {
                imgWidth = maxDimension
                imgHeight = (scaleCoeff * maxDimension).toInt()
            } else {
                imgHeight = maxDimension
                imgWidth = (scaleCoeff * maxDimension).toInt()
            }
        }

        width = imgWidth
        height = imgHeight
        isInitialized = true
    }
}

class TableContentSegmentUiModel(project: Project,
                                 segment: TableContentSegment) : ContentSegmentUiModel(project, segment) {
    val rows = segment.rows.map { TableRowSegmentUiModel(it, project) }
}

class TableRowSegmentUiModel(row: TableRow, project: Project) : UiInteractionModelBase(project) {
    val cells = row.cells.map { TableCellUiModel(it, project) }
}

class TableCellUiModel(cell: TableCell, project: Project) : UiInteractionModelBase(project) {
    val contentSegments = ContentSegmentsUiModel(project, cell.contentSegments)
}