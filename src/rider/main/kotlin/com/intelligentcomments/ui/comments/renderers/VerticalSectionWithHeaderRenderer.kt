package com.intelligentcomments.ui.comments.renderers

import com.intelligentcomments.ui.colors.Colors
import com.intelligentcomments.ui.colors.ColorsProvider
import com.intelligentcomments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectangleModelBuildContributor
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.RectanglesModelUtil
import com.intelligentcomments.ui.util.RectanglesModelUtil.Companion.deltaBetweenHeaderAndContent
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intelligentcomments.ui.util.TextUtil
import com.intelligentcomments.ui.util.TextUtil.Companion.deltaBetweenIconAndTextInHeader
import com.intelligentcomments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

abstract class VerticalSectionWithHeaderRenderer(
  private val section: SectionWithHeaderUiModel
) : Renderer, RectangleModelBuildContributor {

  companion object {
    const val leftContentIndent = 20
    const val leftLineIndent = 7
  }

  private val highlightedText = section.headerUiModel.headerText

  private val icon = section.headerUiModel.icon
  private val shouldRenderContent: Boolean
    get() = section.isExpanded

  final override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    val deltaAfterHeader = if (shouldRenderContent) deltaBetweenHeaderAndContent else 0
    var adjustedRect = TextUtil.renderTextWithIcon(g, rect, editor, highlightedText, icon, 2, deltaAfterHeader)

    if (section.isExpanded) {
      drawLeftLine(g, adjustedRect, editor, additionalRenderInfo)
    }

    adjustedRect = if (!shouldRenderContent) {
      adjustedRect
    } else {
      adjustedRect.x += leftContentIndent
      adjustedRect = renderContent(g, adjustedRect, editor, rectanglesModel, additionalRenderInfo)
      adjustedRect.apply {
        x -= leftContentIndent
      }
    }

    RectanglesModelUtil.addDeltaBetweenSections(adjustedRect)
    return adjustedRect
  }

  private fun drawLeftLine(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ) {
    val contentHeight = calculateContentHeight(editor, additionalRenderInfo)
    val color = section.project.service<ColorsProvider>().getColorFor(Colors.LeftLineBackgroundColor)

    UpdatedGraphicsCookie(g, color = color).use {
      g.fillRoundRect(rect.x + leftLineIndent, rect.y, 2, contentHeight, 3, 3)
    }
  }

  protected abstract fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle

  final override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val height = TextUtil.calculateTextHeightWithIcon(editor, icon, highlightedText)
    return if (shouldRenderContent) height + calculateContentHeight(editor, additionalRenderInfo) else height
  }

  protected abstract fun calculateContentWidth(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int

  final override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    val width = TextUtil.calculateWidthOfTextWithIcon(editor, icon, deltaBetweenIconAndTextInHeader, highlightedText)

    return if (shouldRenderContent) {
      max(width, calculateContentWidth(editor, additionalRenderInfo) + leftContentIndent)
    } else {
      width
    }
  }

  protected abstract fun calculateContentHeight(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int

  final override fun accept(context: RectangleModelBuildContext) {
    val rect = context.rect
    val icon = section.headerUiModel.icon
    val headerText = section.headerUiModel.headerText

    val headerRect = Rectangle(rect).apply {
      height = TextUtil.calculateTextHeightWithIcon(context.editor, icon, headerText)
      width =
        TextUtil.calculateWidthOfTextWithIcon(context.editor, icon, deltaBetweenIconAndTextInHeader, headerText)
    }

    val sectionHeaderModel = section.headerUiModel
    context.rectanglesModel.addElement(sectionHeaderModel, headerRect)
    context.rectanglesModel.addElement(highlightedText.highlighters[0], headerRect)

    RectanglesModelUtil.addHeightDeltaTo(context, headerRect.height)

    val deltaAfterHeader = if (shouldRenderContent) deltaBetweenHeaderAndContent else 0
    RectanglesModelUtil.addHeightDeltaTo(context, deltaAfterHeader)

    if (shouldRenderContent) {
      context.rect.x += leftContentIndent
      context.widthAndHeight.executeWithAdditionalWidth(leftContentIndent) {
        acceptContent(context)
      }

      context.rect.x -= leftContentIndent
    }
  }

  protected abstract fun acceptContent(context: RectangleModelBuildContext)
}