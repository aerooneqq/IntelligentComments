package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HeaderWithBackground
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
    rectanglesModel: RectanglesModel
  ): Rectangle {
    val text = headerUiModel.text
    val color = headerUiModel.backgroundColor
    val adjustedRect = HeaderWithBackground.drawTextWithBackground(g, rect, editorImpl, text, color)

    if (headerUiModel.parent.isExpanded) {
      return renderContent(g, adjustedRect, editorImpl, rectanglesModel)
    }

    return adjustedRect
  }

  protected abstract fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle

  final override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
    val headerHeight = HeaderWithBackground.calculateHeaderHeight(editorImpl)
    val contentHeight = if (headerUiModel.parent.isExpanded) calculateContentHeight(editorImpl) else 0
    return headerHeight + contentHeight
  }

  protected abstract fun calculateContentWidth(editorImpl: EditorImpl): Int

  final override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
    val headerWidth = HeaderWithBackground.calculateHeaderWidth(editorImpl, headerUiModel.text)
    val contentWidth = if (headerUiModel.parent.isExpanded) calculateContentWidth(editorImpl) else 0
    return max(headerWidth, contentWidth)
  }

  protected abstract fun calculateContentHeight(editorImpl: EditorImpl): Int

  final override fun accept(context: RectangleModelBuildContext) {
    val editorImpl = context.editorImpl
    val headerHeight = HeaderWithBackground.calculateHeaderHeight(editorImpl)
    val headerWidth = HeaderWithBackground.calculateHeaderWidth(editorImpl, headerUiModel.text)
    val rect = Rectangle(context.rect)

    val headerRect = Rectangle(rect.x, rect.y, headerWidth, headerHeight)
    context.rectanglesModel.addElement(headerUiModel, headerRect)

    if (headerUiModel.parent.isExpanded) {
      UpdatedRectCookie(context.rect, yDelta = headerHeight).use {
        acceptContent(context)
      }
    }
  }

  protected abstract fun acceptContent(context: RectangleModelBuildContext)
}