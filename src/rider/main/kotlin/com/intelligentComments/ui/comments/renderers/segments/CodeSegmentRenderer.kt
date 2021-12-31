package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.code.CodeSegmentUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

class CodeSegmentRenderer(
  codeSegment: CodeSegmentUiModel
) : TextRendererWithLeftFigure(codeSegment.code) {
  private val lineWidth = 2

  override fun renderLeftFigure(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ) {
    val backgroundColor = editorImpl.project?.service<ColorsProvider>()?.getColorFor(Colors.CodeLeftLineBackgroundColor)

    if (backgroundColor != null) {
      val height = super.calculateExpectedHeightInPixels(editorImpl, additionalRenderInfo)

      UpdatedGraphicsCookie(g, color = backgroundColor).use {
        g.fillRoundRect(rect.x, rect.y + 2, lineWidth, height, 2, 2)
      }
    }
  }

  override fun calculateLeftFigureWidth(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    return lineWidth
  }
}