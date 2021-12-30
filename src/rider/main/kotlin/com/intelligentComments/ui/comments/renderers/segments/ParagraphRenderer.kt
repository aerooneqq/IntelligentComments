package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

class ParagraphRendererImpl(private val model: ParagraphUiModel) : ContentSegmentsRenderer(model.content.content),
  SegmentRenderer {
  companion object {
    private const val leftDelta = 5
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect: Rectangle = rect
    UpdatedRectCookie(rect, xDelta = leftDelta).use {
      adjustedRect = super.render(g, rect, editorImpl, rectanglesModel, additionalRenderInfo)
    }

    return adjustedRect.apply {
      x -= leftDelta
    }
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return super.calculateExpectedWidthInPixels(editorImpl, additionalRenderInfo) + leftDelta
  }

  override fun accept(context: RectangleModelBuildContext) {
  }
}