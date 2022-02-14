package com.intelligentComments.ui.comments.renderers.references

import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer.Companion.deltaBetweenReferences
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

interface ReferencesRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    const val deltaBetweenReferences = 10

    fun getRendererFor(referencesSection: SectionWithHeaderUiModel): ReferencesRenderer {
      return ReferencesRendererImpl(referencesSection)
    }
  }
}

class ReferencesRendererImpl(private val section: SectionWithHeaderUiModel) :
  VerticalSectionWithHeaderRenderer(section), ReferencesRenderer {

  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect = rect

    for (reference in section.content) {
      val renderer = reference.createRenderer()
      adjustedRect = renderer.render(g, adjustedRect, editor, rectanglesModel, additionalRenderInfo)
      adjustedRect.y += deltaBetweenReferences
    }

    adjustedRect.y -= deltaBetweenReferences
    return adjustedRect
  }

  override fun calculateContentHeight(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    var height = 0

    for (reference in section.content) {
      height += reference.createRenderer().calculateExpectedHeightInPixels(editor, additionalRenderInfo)
      height += deltaBetweenReferences
    }

    height -= deltaBetweenReferences
    return height
  }

  override fun calculateContentWidth(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    var width = 0

    for (reference in section.content) {
      val renderer = reference.createRenderer()
      width = max(width, renderer.calculateExpectedWidthInPixels(editor, additionalRenderInfo))
    }

    return width
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    for (reference in section.content) {
      val renderer = reference.createRenderer()
      renderer.accept(context)
      RectanglesModelUtil.updateHeightAndWidthAndAddModel(renderer, context, reference)
      RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, deltaBetweenReferences)
    }

    RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, -deltaBetweenReferences)
  }
}