package com.intelligentComments.ui.util

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.segments.LeftHeaderRightContentRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

data class RenderAdditionalInfo(
  val topmostLeftIndent: Int
) {
  companion object {
    val emptyInstance = RenderAdditionalInfo(0)
  }
}

class ContentSegmentsUtil {
  companion object {
    const val deltaBetweenSegments = 8


    fun renderSegments(
      contentSegmentsUiModel: ContentSegmentsUiModel,
      g: Graphics,
      rect: Rectangle,
      editorImpl: EditorImpl,
      rectanglesModel: RectanglesModel
    ): Rectangle = renderSegments(contentSegmentsUiModel.contentSection.content, g, rect, editorImpl, rectanglesModel)

    fun renderSegments(
      contentSegments: Collection<ContentSegmentUiModel>,
      g: Graphics,
      rect: Rectangle,
      editorImpl: EditorImpl,
      rectanglesModel: RectanglesModel
    ): Rectangle {
      var adjustedRect = Rectangle(rect)
      val additionalRenderInfo = createRenderInfoFor(contentSegments, editorImpl)
      executeWithRenderers(contentSegments) { renderer, _ ->
        adjustedRect = renderer.render(g, adjustedRect, editorImpl, rectanglesModel, additionalRenderInfo)
        RectanglesModelUtil.addHeightDelta(adjustedRect, deltaBetweenSegments)
      }

      RectanglesModelUtil.addHeightDelta(adjustedRect, -deltaBetweenSegments)
      return adjustedRect
    }

    private fun createRenderInfoFor(
      segments: Collection<ContentSegmentUiModel>,
      editorImpl: EditorImpl
    ): RenderAdditionalInfo {
      var maxHeaderWidth = 0
      for (segment in segments) {
        val renderer = SegmentRenderer.getRendererFor(segment)
        if (renderer is LeftHeaderRightContentRenderer) {
          maxHeaderWidth = max(maxHeaderWidth, renderer.calculateHeaderWidth(editorImpl))
        }
      }

      return RenderAdditionalInfo(
        maxHeaderWidth
      )
    }

    private fun executeWithRenderers(
      contentSegments: Collection<ContentSegmentUiModel>,
      action: (SegmentRenderer, ContentSegmentUiModel) -> Unit
    ) {
      for (segment in contentSegments) {
        val renderer = SegmentRenderer.getRendererFor(segment)
        action(renderer, segment)
      }
    }

    fun calculateContentHeight(
      contentSegments: ContentSegmentsUiModel,
      editorImpl: EditorImpl,
      additionalInfo: RenderAdditionalInfo
    ): Int = calculateContentHeight(contentSegments.contentSection.content, editorImpl, additionalInfo)

    fun calculateContentHeight(
      contentSegments: Collection<ContentSegmentUiModel>,
      editorImpl: EditorImpl,
      additionalInfo: RenderAdditionalInfo
    ): Int {
      var height = 0
      executeWithRenderers(contentSegments) { renderer, _ ->
        height += renderer.calculateExpectedHeightInPixels(editorImpl, additionalInfo) + deltaBetweenSegments
      }

      return height - deltaBetweenSegments
    }

    fun calculateContentWidth(
      contentSegments: ContentSegmentsUiModel,
      editorImpl: EditorImpl,
      additionalInfo: RenderAdditionalInfo
    ): Int = calculateContentWidth(contentSegments.contentSection.content, editorImpl, additionalInfo)

    fun calculateContentWidth(
      contentSegments: Collection<ContentSegmentUiModel>,
      editorImpl: EditorImpl,
      additionalInfo: RenderAdditionalInfo
    ): Int {
      var width = 0
      executeWithRenderers(contentSegments) { renderer, _ ->
        width = max(width, renderer.calculateExpectedWidthInPixels(editorImpl, additionalInfo))
      }

      return width
    }

    fun accept(context: RectangleModelBuildContext, segments: Collection<ContentSegmentUiModel>) {
      val additionalRenderInfo = createRenderInfoFor(segments, context.editorImpl)
      val newContext = context.withAdditionalRenderInfo(additionalRenderInfo)

      executeWithRenderers(segments) { renderer, segment ->
        renderer.accept(newContext)
        RectanglesModelUtil.updateHeightAndWidthAndAddModel(renderer, newContext, segment)
        RectanglesModelUtil.addHeightDeltaTo(newContext, deltaBetweenSegments)
      }

      if (segments.isNotEmpty()) {
        RectanglesModelUtil.addHeightDeltaTo(newContext, -deltaBetweenSegments)
      }
    }
  }
}