package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

open class ContentSegmentsRenderer : Renderer, RectangleModelBuildContributor {
  private val segments: Collection<ContentSegmentUiModel>


  constructor(segments: Collection<ContentSegmentUiModel>) {
    this.segments = segments
  }

  constructor(segments: ContentSegmentsUiModel) : this(segments.contentSection.content)


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    return ContentSegmentsUtil.renderSegments(segments, g, rect, editorImpl, rectanglesModel)
  }

  override fun calculateExpectedHeightInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return ContentSegmentsUtil.calculateContentHeight(segments, editorImpl, additionalRenderInfo)
  }

  override fun calculateExpectedWidthInPixels(
    editorImpl: EditorImpl,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return ContentSegmentsUtil.calculateContentWidth(segments, editorImpl, additionalRenderInfo)
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context.createCopy(), segments)
  }
}