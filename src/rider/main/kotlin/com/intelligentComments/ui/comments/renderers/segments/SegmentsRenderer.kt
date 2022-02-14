package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

interface SegmentsRenderer : Renderer, RectangleModelBuildContributor

class DefaultSegmentsRenderer(
  private val section: SectionUiModel
) : ContentSegmentsRenderer(section.content), SegmentsRenderer {
  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context, section.content)
  }
}

class SegmentsRendererWithHeader(
  private val segmentsSection: SectionWithHeaderUiModel
) : VerticalSectionWithHeaderRenderer(segmentsSection), SegmentsRenderer {

  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    return ContentSegmentsUtil.renderSegments(segmentsSection.content, g, rect, editor, rectanglesModel)
  }

  override fun calculateContentHeight(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return ContentSegmentsUtil.calculateContentHeight(segmentsSection.content, editor, additionalRenderInfo)
  }

  override fun calculateContentWidth(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return ContentSegmentsUtil.calculateContentWidth(segmentsSection.content, editor, additionalRenderInfo)
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context, segmentsSection.content)
  }
}