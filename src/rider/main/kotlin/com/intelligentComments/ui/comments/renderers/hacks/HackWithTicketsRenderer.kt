package com.intelligentComments.ui.comments.renderers.hacks

import com.intelligentComments.ui.comments.model.hacks.HackWithTicketsUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

interface HackRenderer : Renderer, RectangleModelBuildContributor

class HackWithTicketsRenderer(
  model: HackWithTicketsUiModel
) : HackRenderer {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    TODO("Not yet implemented")
  }

  override fun calculateExpectedHeightInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    TODO("Not yet implemented")
  }

  override fun calculateExpectedWidthInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    TODO("Not yet implemented")
  }

  override fun accept(context: RectangleModelBuildContext) {
    TODO("Not yet implemented")
  }
}