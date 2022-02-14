package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.model.invariants.InvariantUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer.Companion.gapBetweenInvariants
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RectanglesModelUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface InvariantsRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    const val gapBetweenInvariants = 5

    fun getRendererFor(invariantsSection: SectionWithHeaderUiModel<InvariantUiModel>): InvariantsRenderer {
      return InvariantsRendererImpl(invariantsSection)
    }
  }
}

class InvariantsRendererImpl(private val section: SectionWithHeaderUiModel<InvariantUiModel>) :
  VerticalSectionWithHeaderRenderer<InvariantUiModel>(section), InvariantsRenderer {

  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect = rect
    var maxHeight = 0
    for (invariant in section.content) {
      val renderer = invariant.createRenderer()
      maxHeight = max(renderer.calculateExpectedHeightInPixels(editor, additionalRenderInfo), maxHeight)
    }

    adjustedRect.height = maxHeight

    for (invariant in section.content) {
      val renderer = invariant.createRenderer()
      adjustedRect = renderer.render(g, adjustedRect, editor, rectanglesModel, additionalRenderInfo)
    }

    return Rectangle(rect.x, rect.y + maxHeight, rect.width, rect.height - maxHeight)
  }

  override fun calculateContentHeight(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    var height = 0
    for (invariant in section.content) {
      val renderer = invariant.createRenderer()
      height = max(height, renderer.calculateExpectedHeightInPixels(editor, additionalRenderInfo))
    }

    return height
  }

  override fun calculateContentWidth(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    var width = 0
    for (invariant in section.content) {
      val renderer = invariant.createRenderer()
      width += renderer.calculateWidthWithInvariantInterval(editor, additionalRenderInfo)
    }

    return width
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    val rect = context.rect
    val startX = rect.x
    for (invariant in section.content) {
      val renderer = invariant.createRenderer()
      val height = renderer.calculateExpectedHeightInPixels(context.editor, context.additionalRenderInfo)
      val width = renderer.calculateExpectedWidthInPixels(context.editor, context.additionalRenderInfo)

      context.rectanglesModel.addElement(invariant, Rectangle(rect.x, rect.y, width, height))
      rect.x += width + gapBetweenInvariants
    }

    val height = calculateContentHeight(context.editor, context.additionalRenderInfo)
    val width = calculateContentWidth(context.editor, context.additionalRenderInfo)
    rect.x = startX

    RectanglesModelUtil.addHeightDeltaTo(context, height)
    context.widthAndHeight.updateWidthMax(width)
  }
}