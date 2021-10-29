package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.util.CommentsUtil
import com.intelligentComments.ui.util.CommentsUtil.Companion.deltaBetweenHeaderAndContent
import com.intelligentComments.ui.comments.model.invariants.InvariantUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer.Companion.gapBetweenInvariants
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface InvariantsRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        const val gapBetweenInvariants = 5

        fun getRendererFor(invariantsSection: SectionWithHeaderUiModel<InvariantUiModel>): InvariantsRenderer {
            return InvariantsRendererImpl(invariantsSection)
        }
    }
}

class InvariantsRendererImpl(private val section: SectionWithHeaderUiModel<InvariantUiModel>)
    : VerticalSectionWithHeaderRenderer<InvariantUiModel>(section), InvariantsRenderer {

    override fun renderContent(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               rectanglesModel: RectanglesModel): Rectangle {
        var adjustedRect = rect
        var maxHeight = 0
        for (invariant in section.content) {
            val renderer = InvariantRenderer.getRendererFor(invariant)
            maxHeight = max(renderer.calculateExpectedHeightInPixels(editorImpl), maxHeight)
        }

        adjustedRect.height = maxHeight

        for (invariant in section.content) {
            val renderer = InvariantRenderer.getRendererFor(invariant)
            adjustedRect = renderer.render(g, adjustedRect, editorImpl, rectanglesModel)
        }

        return Rectangle(rect.x, rect.y + maxHeight, rect.width, rect.height - maxHeight)
    }

    override fun calculateContentHeight(editorImpl: EditorImpl): Int {
        var height = 0
        for (invariant in section.content) {
            val renderer = InvariantRenderer.getRendererFor(invariant)
            height = max(height, renderer.calculateExpectedHeightInPixels(editorImpl))
        }

        return height
    }

    override fun calculateContentWidth(editorImpl: EditorImpl): Int {
        var width = 0
        for (invariant in section.content) {
            val renderer = InvariantRenderer.getRendererFor(invariant)
            width += renderer.calculateWidthWithInvariantInterval(editorImpl)
        }

        return width
    }

    override fun acceptContent(context: RectangleModelBuildContext) {
        val rect = context.rect
        CommentsUtil.addHeightDeltaTo(context.widthAndHeight, rect, deltaBetweenHeaderAndContent)

        val startX = rect.x
        for (invariant in section.content) {
            val renderer = InvariantRenderer.getRendererFor(invariant)
            val height = renderer.calculateExpectedHeightInPixels(context.editorImpl)
            val width = renderer.calculateExpectedWidthInPixels(context.editorImpl)

            context.rectanglesModel.addElement(invariant, Rectangle(rect.x, rect.y, width, height))
            rect.x += width + gapBetweenInvariants
        }

        val height = calculateContentHeight(context.editorImpl)
        val width = calculateContentWidth(context.editorImpl)
        rect.x = startX

        CommentsUtil.addHeightDeltaTo(context, height)
        context.widthAndHeight.width = max(context.widthAndHeight.width, width)
    }
}