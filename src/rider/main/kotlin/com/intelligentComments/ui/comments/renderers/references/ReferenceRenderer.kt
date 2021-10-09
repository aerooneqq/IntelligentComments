package com.intelligentComments.ui.comments.renderers.references

import com.intelligentComments.ui.CommentsUtil
import com.intelligentComments.ui.UpdatedGraphicsCookie
import com.intelligentComments.ui.comments.model.DependencyReferenceUiModel
import com.intelligentComments.ui.comments.model.ReferenceUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface ReferenceRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(reference: ReferenceUiModel): ReferenceRenderer {
            return when(reference) {
                is DependencyReferenceUiModel -> DependencyReferenceRenderer(reference)
                else -> throw IllegalArgumentException(reference.toString())
            }
        }
    }
}

class DependencyReferenceRenderer(private val reference: DependencyReferenceUiModel) : ReferenceRenderer {
    companion object {
        const val margin = 5
    }

    private val myNoDescriptionText = "No description was provided"


    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        var adjustedRect = drawBackgroundRoundedRect(g, rect, editorImpl)
        adjustedRect = drawReferenceName(g, adjustedRect, editorImpl)

        if (reference.isExpanded)
            adjustedRect = drawReferenceComments(g, adjustedRect, editorImpl)

        return adjustedRect
    }

    private fun drawBackgroundRoundedRect(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val headerHeight = calculateReferenceNameHeight(editorImpl)
        val headerWidth = calculateHeaderWidth(editorImpl)

        UpdatedGraphicsCookie(g, color = reference.headerUiModel.backgroundColor).use {
            g.fillRoundRect(rect.x, rect.y, headerWidth, headerHeight, 3, 3)
        }

        return rect
    }

    private fun calculateHeaderWidth(editorImpl: EditorImpl): Int {
        val textWidth = CommentsUtil.getTextWidth(editorImpl, reference.referenceName)
        return textWidth + 2 * margin
    }

    private fun drawReferenceName(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val shift = calculateHeightShiftForReferenceName(editorImpl)
        val rectForText = Rectangle(rect.x + margin, rect.y - shift, rect.width, rect.height)
        CommentsUtil.renderText(g, rectForText, editorImpl, reference.referenceName, 0)

        val height = calculateReferenceNameHeight(editorImpl)
        return Rectangle(rect.x, rect.y + height, rect.width, rect.height - height)
    }

    private fun calculateHeightShiftForReferenceName(editorImpl: EditorImpl) = CommentsUtil.getTextHeight(editorImpl, null) / 4

    private fun drawReferenceComments(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val shift = calculateHeightShiftForReferenceName(editorImpl)
        val adjustedRect = Rectangle(rect).apply {
            y -= shift
        }

        if (reference.dependencyDescription.isEmpty()) {
            CommentsUtil.renderText(g, adjustedRect, editorImpl, myNoDescriptionText, 0)
        } else {
            CommentsUtil.renderLines(g, adjustedRect, editorImpl, getLines(), 0)
        }

        val heightDelta = calculateDescriptionHeight(editorImpl)
        return Rectangle(rect).apply {
            y += heightDelta
            height =- heightDelta
        }
    }

    private fun calculateDescriptionHeight(editorImpl: EditorImpl): Int {
        val textHeight = CommentsUtil.getTextHeight(editorImpl, null)
        return if (reference.dependencyDescription.isEmpty()) {
            textHeight
        } else {
            getLines().size * textHeight
        }
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        val referenceNameHeight = calculateReferenceNameHeight(editorImpl)

        val descriptionHeight = if (reference.isExpanded) {
            calculateDescriptionHeight(editorImpl)
        } else {
            0
        }

        return referenceNameHeight + descriptionHeight
    }

    private fun getLines() = reference.dependencyDescription.split('\n')

    private fun calculateReferenceNameHeight(editorImpl: EditorImpl): Int {
        return CommentsUtil.getTextHeight(editorImpl, null)
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        val metrics = CommentsUtil.getFontMetrics(editorImpl, null)
        var maxWidth = CommentsUtil.getTextWidth(metrics, reference.referenceName)
        maxWidth = max(maxWidth, calculateMaxWidthInDescription(editorImpl))

        return maxWidth
    }

    private fun calculateMaxWidthInDescription(editorImpl: EditorImpl): Int {
        if (reference.dependencyDescription.isEmpty())
            return CommentsUtil.getTextWidth(editorImpl, myNoDescriptionText)

        var maxWidth = 0
        for (line in getLines()) {
            maxWidth = max(CommentsUtil.getTextWidth(editorImpl, line), maxWidth)
        }

        return maxWidth
    }

    override fun accept(context: RectangleModelBuildContext) {
        val editorImpl = context.editorImpl
        val headerHeight = calculateReferenceNameHeight(editorImpl)
        val headerWidth = calculateHeaderWidth(editorImpl)
        val rect = Rectangle(context.rect)

        val headerRect = Rectangle(rect.x, rect.y, headerWidth, headerHeight)
        context.rectanglesModel.addElement(reference.headerUiModel, headerRect)

        if (reference.isExpanded) {
            rect.y += calculateReferenceNameHeight(editorImpl)
            val textWidth = calculateMaxWidthInDescription(editorImpl)
            val textHeight = calculateDescriptionHeight(editorImpl)

            val textRect = Rectangle(rect.x, rect.y, textWidth, textHeight)
            context.rectanglesModel.addElement(reference.descriptionUiModel, textRect)
        }
    }
}