package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.model.invariants.AddNewInvariantUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

class AddNewInvariantRenderer(private val model: AddNewInvariantUiModel) : InvariantRenderer {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val width = calculateWidth(editorImpl)
    val background = editorImpl.contentComponent.background
    return InvariantRendererUtil.render(
      g,
      rect,
      editorImpl,
      model.borderColor,
      background,
      width,
      model.text,
      model.icon
    )
  }

  private fun calculateWidth(editorImpl: EditorImpl): Int {
    val iconDelta = InvariantRenderer.deltaBetweenTextAndIcon
    val extraWidth = InvariantRenderer.extraInvariantWidth
    val iconWidth = model.icon.iconWidth
    val textWidth = TextUtil.getTextWidth(editorImpl, model.text)

    return textWidth + iconWidth + extraWidth + iconDelta
  }

  override fun calculateWidthWithInvariantInterval(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return calculateExpectedWidthInPixels(editorImpl, additionalRenderInfo) + InvariantsRenderer.gapBetweenInvariants
  }

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo)
  : Int = InvariantRenderer.invariantHeight

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int = calculateWidth(editorImpl)
}