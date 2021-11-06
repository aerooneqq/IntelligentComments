package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.authors.CommentAuthorsRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.comments.renderers.todos.ToDosRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.use
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle

class IntelligentCommentsRenderer(private val model: IntelligentCommentUiModel) : RendererWithRectangleModel(model) {
    companion object {
        private const val leftLineWidth = 2
        private const val deltaBetweenLeftLineAndContent = 10

        private val borderDeltas = Dimension(0, 0)
    }

    override val xDelta = deltaBetweenLeftLineAndContent + borderDeltas.width
    override  val yDelta = borderDeltas.height


    override fun paintInternal(
        inlay: Inlay<*>,
        g: Graphics,
        targetRegion: Rectangle,
        textAttributes: TextAttributes,
        colorsProvider: ColorsProvider
    ) {
        val editorImpl = inlay.editor as? EditorImpl ?: return

        var adjustedRect = adjustContentRect(targetRegion)
        val leftLineBackgroundColor = colorsProvider.getColorFor(Colors.LeftLineBackgroundColor)
        UpdatedGraphicsCookie(g, color = leftLineBackgroundColor).use {
            adjustedRect = drawLeftLine(g, adjustedRect)
        }

        adjustedRect = drawCommentAuthors(g, adjustedRect, editorImpl)
        adjustedRect = drawCommentContent(g, adjustedRect, editorImpl)
        adjustedRect = drawReferences(g, adjustedRect, editorImpl)
        adjustedRect = drawInvariants(g, adjustedRect, editorImpl)
        adjustedRect = drawToDos(g, adjustedRect, editorImpl)
    }

    private fun adjustContentRect(rect: Rectangle): Rectangle {
        val w = borderDeltas.width
        val h = borderDeltas.height

        return Rectangle(rect.x + w, rect.y + h, rect.width - w, rect.height - h)
    }

    private fun drawLeftLine(g: Graphics, rect: Rectangle): Rectangle {
        g.fillRoundRect(rect.x, rect.y, leftLineWidth, rect.height, 2, 2)
        return Rectangle(rect.x + deltaBetweenLeftLineAndContent, rect.y, rect.width - deltaBetweenLeftLineAndContent, rect.height)
    }

    private fun drawCommentAuthors(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = CommentAuthorsRenderer.getRendererFor(model.authorsSection.content)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawCommentContent(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val model = getOrCreateRectanglesModel(editorImpl)
        val renderer = SegmentsRenderer.getRendererFor(this.model.contentSection)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawReferences(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = ReferencesRenderer.getRendererFor(model.referencesSection)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawInvariants(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = InvariantsRenderer.getRendererFor(model.invariantsSection)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawToDos(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = ToDosRenderer.getRendererFor(model.todosSection)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }
}