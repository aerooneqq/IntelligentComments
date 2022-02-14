package com.intelligentComments.ui.core

import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

interface Renderer : RectangleModelBuildContributor {
  fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle

  fun calculateExpectedHeightInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int
  fun calculateExpectedWidthInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int
}


interface RectangleModelBuildContributor {
  fun accept(context: RectangleModelBuildContext)
}