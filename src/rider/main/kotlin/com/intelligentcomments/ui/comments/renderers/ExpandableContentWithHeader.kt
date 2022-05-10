package com.intelligentcomments.ui.comments.renderers

import com.intelligentcomments.ui.comments.model.ExpandableUiModel
import com.intelligentcomments.ui.comments.model.HeaderUiModel
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectangleModelBuildContributor
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HeaderWithBackground
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intelligentcomments.ui.util.UpdatedRectCookie
import com.intellij.openapi.editor.Editor
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
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val text = headerUiModel.text
    val color = headerUiModel.backgroundColor
    val adjustedRect = HeaderWithBackground.drawTextWithBackground(g, rect, editor, text, color)

    if (isExpanded()) {
      return renderContent(g, adjustedRect, editor, rectanglesModel)
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
    editor: Editor,
    rectanglesModel: RectanglesModel
  ): Rectangle

  final override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val headerHeight = HeaderWithBackground.calculateHeaderHeight(editor)
    val contentHeight = if (isExpanded()) calculateContentHeight(editor, additionalRenderInfo) else 0
    return headerHeight + contentHeight
  }

  protected abstract fun calculateContentWidth(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int

  final override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val headerWidth = HeaderWithBackground.calculateHeaderWidth(editor, headerUiModel.text)
    val contentWidth = if (isExpanded()) calculateContentWidth(editor, additionalRenderInfo) else 0
    return max(headerWidth, contentWidth)
  }

  protected abstract fun calculateContentHeight(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int

  final override fun accept(context: RectangleModelBuildContext) {
    val editor = context.editor
    val headerHeight = HeaderWithBackground.calculateHeaderHeight(editor)
    val headerWidth = HeaderWithBackground.calculateHeaderWidth(editor, headerUiModel.text)
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