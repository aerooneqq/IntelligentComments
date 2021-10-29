package com.intelligentComments.ui.comments.renderers.references

import com.intelligentComments.ui.util.CommentsUtil
import com.intelligentComments.ui.comments.model.DependencyReferenceUiModel
import com.intelligentComments.ui.comments.model.ReferenceUiModel
import com.intelligentComments.ui.comments.renderers.ExpandableContentWithHeader
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
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

class DependencyReferenceRenderer(private val reference: DependencyReferenceUiModel) : ExpandableContentWithHeader(reference.headerUiModel), ReferenceRenderer {
    private val myNoDescriptionText = "No description was provided"


    override fun renderContent(g: Graphics,
                               rect: Rectangle,
                               editorImpl: EditorImpl,
                               rectanglesModel: RectanglesModel): Rectangle {
        return drawReferenceComments(g, rect, editorImpl)
    }

    private fun drawReferenceComments(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val adjustedRect = Rectangle(rect)

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

    override fun calculateContentHeight(editorImpl: EditorImpl): Int {
        return calculateDescriptionHeight(editorImpl)
    }

    private fun getLines() = reference.dependencyDescription.split('\n')

    override fun calculateContentWidth(editorImpl: EditorImpl): Int {
        return calculateMaxWidthInDescription(editorImpl)
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

    override fun acceptContent(context: RectangleModelBuildContext) {
        val editorImpl = context.editorImpl
        val rect = context.rect

        val textWidth = calculateMaxWidthInDescription(editorImpl)
        val textHeight = calculateDescriptionHeight(editorImpl)

        val textRect = Rectangle(rect.x, rect.y, textWidth, textHeight)
        context.rectanglesModel.addElement(reference.descriptionUiModel, textRect)
    }
}