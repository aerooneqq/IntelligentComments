package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intelligentcomments.ui.util.UpdatedRectCookie
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

class ParagraphRenderer(
  model: ParagraphUiModel
) : ContentSegmentsRenderer(model.content), SegmentRenderer {
  companion object {
    private const val leftDelta = 0
  }


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect: Rectangle = rect
    UpdatedRectCookie(rect, xDelta = leftDelta).use {
      adjustedRect = super.render(g, rect, editor, rectanglesModel, additionalRenderInfo)
    }

    return adjustedRect.apply {
      x -= leftDelta
    }
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return super.calculateExpectedWidthInPixels(editor, additionalRenderInfo) + leftDelta
  }
}