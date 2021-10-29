package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

interface SegmentsRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(segmentsSection: SectionWithHeaderUiModel<ContentSegmentUiModel>): SegmentsRenderer {
            return SegmentsRendererImpl(segmentsSection)
        }
    }
}

class SegmentsRendererImpl(private val segmentsSection: SectionWithHeaderUiModel<ContentSegmentUiModel>)
    : VerticalSectionWithHeaderRenderer<ContentSegmentUiModel>(segmentsSection), SegmentsRenderer {

    override fun renderContent(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               rectanglesModel: RectanglesModel): Rectangle {
        return ContentSegmentsUtil.renderSegments(segmentsSection.content, g, rect, editorImpl, rectanglesModel)
    }

    override fun calculateContentHeight(editorImpl: EditorImpl): Int {
        return ContentSegmentsUtil.calculateContentHeight(segmentsSection.content, editorImpl)
    }

    override fun calculateContentWidth(editorImpl: EditorImpl): Int {
        return ContentSegmentsUtil.calculateContentWidth(segmentsSection.content, editorImpl)
    }

    override fun acceptContent(context: RectangleModelBuildContext) {
        ContentSegmentsUtil.accept(context, segmentsSection.content)
    }
}