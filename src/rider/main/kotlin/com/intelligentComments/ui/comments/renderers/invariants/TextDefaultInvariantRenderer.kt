package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.model.invariants.TextInvariantUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

class TextDefaultInvariantRenderer(private val model: TextInvariantUiModel) : InvariantRenderer {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val width = calculateWidth(editorImpl)
    return InvariantRendererUtil.render(
      g,
      rect,
      editorImpl,
      model.borderColor,
      model.backgroundColor,
      width,
      model.text
    )
  }

  private fun calculateWidth(editorImpl: EditorImpl): Int {
    return TextUtil.getTextWidth(editorImpl, model.text) + InvariantRenderer.extraInvariantWidth
  }

  override fun calculateWidthWithInvariantInterval(
    editorImpl: EditorImpl,
    additionalRendererInfo: RenderAdditionalInfo
  ): Int {
    return calculateExpectedWidthInPixels(editorImpl, additionalRendererInfo) + InvariantsRenderer.gapBetweenInvariants
  }

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int = InvariantRenderer.invariantHeight

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int = calculateWidth(editorImpl)
}