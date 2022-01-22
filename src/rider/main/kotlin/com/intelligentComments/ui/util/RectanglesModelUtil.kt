package com.intelligentComments.ui.util

import com.intelligentComments.ui.comments.model.*
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
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

data class RectanglesModelBuildResult(val model: RectanglesModel, val xShift: Int, val yShift: Int)

class RectanglesModelUtil {
  companion object {
    const val deltaBetweenHeaderAndContent = 5
    const val heightDeltaBetweenSections = 10


    fun buildRectanglesModel(
      editorImpl: EditorImpl,
      model: UiInteractionModelBase,
      xDelta: Int,
      yDelta: Int
    ): RectanglesModelBuildResult {
      return when (model) {
        is IntelligentCommentUiModel -> buildRectanglesModel(editorImpl, model, xDelta, yDelta)
        is DocCommentUiModel -> buildRectanglesModel(editorImpl, model,  model.contentSection, xDelta, yDelta)
        is CommentWithOneTextSegmentUiModel -> buildRectanglesModel(editorImpl, model, model.contentSection, xDelta, yDelta)
        is CollapsedCommentUiModel -> buildRectanglesModel(editorImpl, model, model.contentSection, xDelta, yDelta)
        else -> throw IllegalArgumentException(model.toString())
      }
    }

    private fun buildRectanglesModel(
      editorImpl: EditorImpl,
      model: UiInteractionModelBase,
      contentSection: SectionUiModel<ContentSegmentUiModel>,
      xDelta: Int,
      yDelta: Int
    ): RectanglesModelBuildResult {
      val context = createRectanglesBuildContext(xDelta, yDelta, editorImpl)

      val renderer = SegmentsRenderer.getRendererFor(contentSection)
      renderer.accept(context)

      addTopmostModel(context, xDelta, yDelta, model)

      val delta = shiftRectanglesYIfNeeded(editorImpl, context)
      context.rectanglesModel.apply {
        setSize(context.widthAndHeight.width, context.widthAndHeight.height)
        seal()
      }

      return RectanglesModelBuildResult(context.rectanglesModel, 0, delta)
    }

    private fun shiftRectanglesYIfNeeded(editorImpl: EditorImpl, context: RectangleModelBuildContext): Int {
      val height = context.widthAndHeight.height
      val lineHeight = editorImpl.lineHeight

      if (height < lineHeight) {
        val delta = lineHeight - height
        context.rectanglesModel.shiftAllRectanglesY(delta)
        return delta
      }

      return 0
    }

    private fun addTopmostModel(
      context: RectangleModelBuildContext,
      xDelta: Int,
      yDelta: Int,
      model: UiInteractionModelBase
    ) {
      val widthAndHeight = context.widthAndHeight
      val rectanglesModel = context.rectanglesModel
      val overallRect = Rectangle(xDelta, yDelta, widthAndHeight.width, widthAndHeight.height)
      rectanglesModel.addElement(model, overallRect)
    }

    private fun createRectanglesBuildContext(
      xDelta: Int,
      yDelta: Int,
      editorImpl: EditorImpl
    ): RectangleModelBuildContext {
      val widthAndHeight = WidthAndHeight()
      val initialRect = Rectangle(xDelta, yDelta, Int.MAX_VALUE, Int.MAX_VALUE)
      val model = RectanglesModel()
      return RectangleModelBuildContext(model, widthAndHeight, initialRect, editorImpl)
    }

    private fun updateRectYAndHeight(delta: Int, context: RectangleModelBuildContext) {
      context.rect.y += delta
      context.widthAndHeight.updateHeightSum(delta)
    }

    private fun buildRectanglesModel(
      editorImpl: EditorImpl,
      intelligentComment: IntelligentCommentUiModel,
      xDelta: Int,
      yDelta: Int
    ): RectanglesModelBuildResult {
      val context = createRectanglesBuildContext(xDelta, yDelta, editorImpl)

      CommentAuthorsRenderer.getRendererFor(intelligentComment.authorsSection.content).accept(context)
      updateRectYAndHeight(heightDeltaBetweenSections, context)

      SegmentsRenderer.getRendererFor(intelligentComment.contentSection).accept(context)
      updateRectYAndHeight(heightDeltaBetweenSections, context)

      ReferencesRenderer.getRendererFor(intelligentComment.referencesSection).accept(context)
      updateRectYAndHeight(heightDeltaBetweenSections, context)

      InvariantsRenderer.getRendererFor(intelligentComment.invariantsSection).accept(context)
      updateRectYAndHeight(heightDeltaBetweenSections, context)

      ToDosRenderer.getRendererFor(intelligentComment.todosSection).accept(context)
      updateRectYAndHeight(heightDeltaBetweenSections, context)

      addTopmostModel(context, xDelta, yDelta, intelligentComment)

      val model = context.rectanglesModel.apply {
        setSize(context.widthAndHeight.width, context.widthAndHeight.height)
        seal()
      }

      return RectanglesModelBuildResult(model, 0, 0)
    }

    fun addDeltaBetweenSections(rect: Rectangle) {
      addHeightDelta(rect, heightDeltaBetweenSections)
    }

    fun addHeightDelta(rect: Rectangle, delta: Int) {
      rect.y += delta
      rect.height -= delta
    }

    fun updateHeightAndWidthAndAddModel(
      renderer: Renderer,
      context: RectangleModelBuildContext,
      uiInteractionModel: UiInteractionModelBase
    ) {
      val width = renderer.calculateExpectedWidthInPixels(context.editorImpl, context.additionalRenderInfo)
      val height = renderer.calculateExpectedHeightInPixels(context.editorImpl, context.additionalRenderInfo)

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