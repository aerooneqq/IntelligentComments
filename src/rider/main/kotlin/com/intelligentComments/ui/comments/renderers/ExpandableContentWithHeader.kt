package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HeaderWithBackground
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.Integer.max

abstract class ExpandableContentWithHeader(
  private val headerUiModel: HeaderUiModel
) : Renderer, RectangleModelBuildContributor {
  final override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val text = headerUiModel.text
    val color = headerUiModel.backgroundColor
    val adjustedRect = HeaderWithBackground.drawTextWithBackground(g, rect, editorImpl, text, color)

    if (isExpanded()) {
      return renderContent(g, adjustedRect, editorImpl, rectanglesModel)
    }

    return adjustedRect
  }

  private fun isExpanded(): Boolean {
    val parent = headerUiModel.parent
    parent as ExpandableUiModel
    return parent.isExpanded
  }

  protected abstract fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle

  final override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val headerHeight = HeaderWithBackground.calculateHeaderHeight(editorImpl)
    val contentHeight = if (isExpanded()) calculateContentHeight(editorImpl, additionalRenderInfo) else 0
    return headerHeight + contentHeight
  }

  protected abstract fun calculateContentWidth(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int

  final override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val headerWidth = HeaderWithBackground.calculateHeaderWidth(editorImpl, headerUiModel.text)
    val contentWidth = if (isExpanded()) calculateContentWidth(editorImpl, additionalRenderInfo) else 0
    return max(headerWidth, contentWidth)
  }

  protected abstract fun calculateContentHeight(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int

  final override fun accept(context: RectangleModelBuildContext) {
    val editorImpl = context.editorImpl
    val headerHeight = HeaderWithBackground.calculateHeaderHeight(editorImpl)
    val headerWidth = HeaderWithBackground.calculateHeaderWidth(editorImpl, headerUiModel.text)
    val rect = Rectangle(context.rect)

    val headerRect = Rectangle(rect.x, rect.y, headerWidth, headerHeight)
    context.rectanglesModel.addElement(headerUiModel, headerRect)

    if (isExpanded()) {
      UpdatedRectCookie(context.rect, yDelta = headerHeight).use {
        acceptContent(context)
      }
    }
  }

  protected abstract fun acceptContent(context: RectangleModelBuildContext)
}