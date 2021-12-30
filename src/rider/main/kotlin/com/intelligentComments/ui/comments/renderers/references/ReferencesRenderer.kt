package com.intelligentComments.ui.comments.renderers.references

import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer.Companion.deltaBetweenReferences
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RectanglesModelUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface ReferencesRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    const val deltaBetweenReferences = 10

    fun getRendererFor(referencesSection: SectionWithHeaderUiModel<ReferenceUiModel>): ReferencesRenderer {
      return ReferencesRendererImpl(referencesSection)
    }
  }
}

class ReferencesRendererImpl(private val section: SectionWithHeaderUiModel<ReferenceUiModel>) :
  VerticalSectionWithHeaderRenderer<ReferenceUiModel>(section), ReferencesRenderer {

  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    var adjustedRect = rect

    for (reference in section.content) {
      val renderer = ReferenceRenderer.getRendererFor(reference)
      adjustedRect = renderer.render(g, adjustedRect, editorImpl, rectanglesModel, additionalRenderInfo)
      adjustedRect.y += deltaBetweenReferences
    }

    adjustedRect.y -= deltaBetweenReferences
    return adjustedRect
  }

  override fun calculateContentHeight(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    var height = 0

    for (reference in section.content) {
      height += ReferenceRenderer.getRendererFor(reference).calculateExpectedHeightInPixels(editorImpl, additionalRenderInfo)
      height += deltaBetweenReferences
    }

    height -= deltaBetweenReferences
    return height
  }

  override fun calculateContentWidth(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    var width = 0

    for (reference in section.content) {
      val renderer = ReferenceRenderer.getRendererFor(reference)
      width = max(width, renderer.calculateExpectedWidthInPixels(editorImpl, additionalRenderInfo))
    }

    return width
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    for (reference in section.content) {
      val renderer = ReferenceRenderer.getRendererFor(reference)
      renderer.accept(context)
      RectanglesModelUtil.updateHeightAndWidthAndAddModel(renderer, context, reference)
      RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, deltaBetweenReferences)
    }

    RectanglesModelUtil.addHeightDeltaTo(context.widthAndHeight, context.rect, -deltaBetweenReferences)
  }
}