package com.intelligentComments.ui.core

import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.WidthAndHeight
import com.intellij.openapi.editor.Editor
import java.awt.Rectangle

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
    return RectangleModelBuildContext(
      rectanglesModel,
      WidthAndHeight(widthAndHeight),
      newRectangle,
      editor,
      additionalRenderInfo
    )
  }

  fun withAdditionalRenderInfo(info: RenderAdditionalInfo): RectangleModelBuildContext {
    return RectangleModelBuildContext(rectanglesModel, widthAndHeight, rect, editor, info)
  }
}