package com.intelligentComments.ui.core

import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.WidthAndHeight
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

interface Renderer {
  fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle

  fun calculateExpectedHeightInPixels(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int
  fun calculateExpectedWidthInPixels(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int
}


data class RectangleModelBuildContext(
  val rectanglesModel: RectanglesModel,
  val widthAndHeight: WidthAndHeight,
  val rect: Rectangle,
  val editorImpl: EditorImpl,
  val additionalRenderInfo: RenderAdditionalInfo = RenderAdditionalInfo.emptyInstance
) {
  fun withRectangle(newRectangle: Rectangle): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, widthAndHeight, newRectangle, editorImpl, additionalRenderInfo)
  }

  fun copyWithNewRect(newRectangle: Rectangle): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, WidthAndHeight(widthAndHeight), newRectangle, editorImpl, additionalRenderInfo)
  }

  fun withAdditionalRenderInfo(info: RenderAdditionalInfo): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, widthAndHeight, rect, editorImpl, info)
  }
}

interface RectangleModelBuildContributor {
  fun accept(context: RectangleModelBuildContext)
}