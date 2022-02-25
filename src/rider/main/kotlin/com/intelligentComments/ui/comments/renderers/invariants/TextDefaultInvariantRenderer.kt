package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.model.content.invariants.TextInvariantUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

class TextDefaultInvariantRenderer(private val model: TextInvariantUiModel) : InvariantRenderer {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val width = calculateWidth(editor)
    return InvariantRendererUtil.render(
      g,
      rect,
      editor,
      model.borderColor,
      model.backgroundColor,
      width,
      model.name.text
    )
  }

  private fun calculateWidth(editor: Editor): Int {
    return TextUtil.getTextWidth(editor, model.name.text) + InvariantRenderer.extraInvariantWidth
  }

  override fun calculateWidthWithInvariantInterval(
    editor: Editor,
    additionalRendererInfo: RenderAdditionalInfo
  ): Int {
    return calculateExpectedWidthInPixels(editor, additionalRendererInfo) + InvariantsRenderer.gapBetweenInvariants
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int = InvariantRenderer.invariantHeight

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int = calculateWidth(editor)

  override fun accept(context: RectangleModelBuildContext) {
  }
}