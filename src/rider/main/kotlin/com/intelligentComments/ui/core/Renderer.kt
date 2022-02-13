package com.intelligentComments.ui.core

import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.WidthAndHeight
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

interface Renderer {
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


data class RectangleModelBuildContext(
  val rectanglesModel: RectanglesModel,
  val widthAndHeight: WidthAndHeight,
  val rect: Rectangle,
  val editor: Editor,
  val additionalRenderInfo: RenderAdditionalInfo = RenderAdditionalInfo.emptyInstance
) {
  fun withRectangle(newRectangle: Rectangle): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, widthAndHeight, newRectangle, editor, additionalRenderInfo)
  }

  fun createCopy(): RectangleModelBuildContext {
    return createCopy(Rectangle(rect))
  }

  fun createCopy(newRectangle: Rectangle): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, WidthAndHeight(widthAndHeight), newRectangle, editor, additionalRenderInfo)
  }

  fun withAdditionalRenderInfo(info: RenderAdditionalInfo): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, widthAndHeight, rect, editor, info)
  }
}

interface RectangleModelBuildContributor {
  fun accept(context: RectangleModelBuildContext)
}