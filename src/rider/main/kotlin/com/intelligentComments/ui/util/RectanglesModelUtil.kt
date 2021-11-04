package com.intelligentComments.ui.util

import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.authors.CommentAuthorsRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.comments.renderers.todos.ToDosRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Rectangle

class RectanglesModelUtil {
    companion object {
        const val deltaBetweenHeaderAndContent = 5
        const val heightDeltaBetweenSections = 10
        private const val minCommentHeightPx = 0

        fun buildRectanglesModel(editorImpl: EditorImpl,
                                 intelligentComment: IntelligentCommentUiModel,
                                 xDelta: Int,
                                 yDelta: Int): RectanglesModel {
            val widthAndHeight = WidthAndHeight().apply {
                updateHeightSum(minCommentHeightPx)
            }

            val initialRect = Rectangle(xDelta, yDelta, Int.MAX_VALUE, Int.MAX_VALUE)
            val model = RectanglesModel()
            val buildContext = RectangleModelBuildContext(model, widthAndHeight, initialRect, editorImpl)

            fun updateRectYAndHeight(delta: Int) {
                initialRect.y += delta
                widthAndHeight.updateHeightSum(delta)
            }

            CommentAuthorsRenderer.getRendererFor(intelligentComment.authorsSection.content).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            SegmentsRenderer.getRendererFor(intelligentComment.contentSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            ReferencesRenderer.getRendererFor(intelligentComment.referencesSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            InvariantsRenderer.getRendererFor(intelligentComment.invariantsSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            ToDosRenderer.getRendererFor(intelligentComment.todosSection).accept(buildContext)
            updateRectYAndHeight(heightDeltaBetweenSections)

            model.addElement(intelligentComment, Rectangle(xDelta, yDelta, widthAndHeight.width, widthAndHeight.height))

            return model.apply {
                setSize(widthAndHeight.width, widthAndHeight.height)
                seal()
            }
        }

        fun addDeltaBetweenSections(rect: Rectangle) {
            addHeightDelta(rect, heightDeltaBetweenSections)
        }

        fun addHeightDelta(rect: Rectangle, delta: Int) {
            rect.y += delta
            rect.height -= delta
        }

        fun updateHeightAndWidthAndAddModel(renderer: Renderer,
                                            context: RectangleModelBuildContext,
                                            uiInteractionModel: UiInteractionModelBase) {
            val width = renderer.calculateExpectedWidthInPixels(context.editorImpl)
            val height = renderer.calculateExpectedHeightInPixels(context.editorImpl)

            context.widthAndHeight.updateHeightSum(height)
            context.widthAndHeight.updateWidthMax(width)

            val rect = context.rect
            context.rectanglesModel.addElement(uiInteractionModel, Rectangle(rect.x, rect.y, width, height))
            rect.y += height
        }

        fun addHeightDeltaTo(widthAndHeight: WidthAndHeight, rect: Rectangle, delta: Int) {
            widthAndHeight.updateHeightSum(delta)
            rect.y += delta
        }

        fun addHeightDeltaTo(context: RectangleModelBuildContext, delta: Int) {
            addHeightDeltaTo(context.widthAndHeight, context.rect, delta)
        }
    }
}