package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.model.invariants.AddNewInvariantUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

class AddNewInvariantRenderer(private val model: AddNewInvariantUiModel) : InvariantRenderer {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val width = calculateWidth(editor)
    val background = editor.contentComponent.background
    return InvariantRendererUtil.render(
      g,
      rect,
      editor,
      model.borderColor,
      background,
      width,
      model.text,
      model.icon
    )
  }

  private fun calculateWidth(editor: Editor): Int {
    val iconDelta = InvariantRenderer.deltaBetweenTextAndIcon
    val extraWidth = InvariantRenderer.extraInvariantWidth
    val iconWidth = model.icon.iconWidth
    val textWidth = TextUtil.getTextWidth(editor, model.text)

    return textWidth + iconWidth + extraWidth + iconDelta
  }

  override fun calculateWidthWithInvariantInterval(
    editor: Editor,
    additionalRendererInfo: RenderAdditionalInfo
  ): Int {
    return calculateExpectedWidthInPixels(editor, additionalRendererInfo) + InvariantsRenderer.gapBetweenInvariants
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo)
  : Int = InvariantRenderer.invariantHeight

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int = calculateWidth(editor)


  override fun accept(context: RectangleModelBuildContext) {
  }
}