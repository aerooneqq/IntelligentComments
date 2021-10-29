package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.util.CommentsUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.comments.renderers.todos.ToDosRenderer
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.RectanglesModelHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.use
import com.jetbrains.rd.platform.util.application
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle

class IntelligentCommentsRenderer(private val intelligentComment: IntelligentCommentUiModel) : EditorCustomElementRenderer {
    companion object {
        private const val leftLineWidth = 2
        private const val deltaBetweenLeftLineAndContent = 10

        private val borderDeltas = Dimension(0, 5)
    }

    private val myRectanglesModelHolder = RectanglesModelHolder(intelligentComment)
    val rectanglesModel
        get() = myRectanglesModelHolder.model


    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        application.assertIsDispatchThread()
        return calculateExpectedWith(inlay.editor as EditorImpl)
    }

    private fun calculateExpectedWith(editorImpl: EditorImpl) = getOrCreateRectanglesModel(editorImpl).width

    override fun calcHeightInPixels(inlay: Inlay<*>): Int {
        application.assertIsDispatchThread()
        return calculateExpectedHeight(inlay.editor as EditorImpl)
    }

    private fun calculateExpectedHeight(editorImpl: EditorImpl) = getOrCreateRectanglesModel(editorImpl).height

    private fun getOrCreateRectanglesModel(editorImpl: EditorImpl): RectanglesModel {
        val xDelta = deltaBetweenLeftLineAndContent + borderDeltas.width
        val yDelta = borderDeltas.height
        return myRectanglesModelHolder.revalidate(editorImpl, xDelta, yDelta)
    }

    override fun paint(inlay: Inlay<*>, g: Graphics, rect: Rectangle, textAttributes: TextAttributes) {
        application.assertIsDispatchThread()
        val project = inlay.editor.project ?: return

        val colorsProvider = project.service<ColorsProvider>()
        val defaultTextColor = colorsProvider.getColorFor(Colors.TextDefaultColor)
        UpdatedGraphicsCookie(g, defaultTextColor, CommentsUtil.font).use {
            val editorImpl = inlay.editor as? EditorImpl ?: return

            var adjustedRect = adjustContentRect(rect)
            val leftLineBackgroundColor = colorsProvider.getColorFor(Colors.LeftLineBackgroundColor)
            UpdatedGraphicsCookie(g, color = leftLineBackgroundColor).use {
                adjustedRect = drawLeftLine(g, adjustedRect)
            }

            adjustedRect = drawCommentAuthors(g, adjustedRect, editorImpl)
            adjustedRect = drawCommentContent(g, adjustedRect, editorImpl)
            adjustedRect = drawReferences(g, adjustedRect, editorImpl)
            adjustedRect = drawInvariants(g, adjustedRect, editorImpl)
            adjustedRect = drawToDos(g, adjustedRect, editorImpl)

            for (rectangle in rectanglesModel!!.allRectangles) {
                g.drawRect(rectangle.x + rect.x, rectangle.y + rect.y, rectangle.width, rectangle.height)
            }
        }
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
        val renderer = CommentAuthorsRenderer.getRendererFor(intelligentComment.authorsSection.content)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawCommentContent(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val model = getOrCreateRectanglesModel(editorImpl)
        val renderer = SegmentsRenderer.getRendererFor(intelligentComment.contentSection)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawReferences(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = ReferencesRenderer.getRendererFor(intelligentComment.referencesSection)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawInvariants(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = InvariantsRenderer.getRendererFor(intelligentComment.invariantsSection)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }

    private fun drawToDos(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val renderer = ToDosRenderer.getRendererFor(intelligentComment.todosSection)
        val model = getOrCreateRectanglesModel(editorImpl)
        return renderer.render(g, rect, editorImpl, model)
    }
}