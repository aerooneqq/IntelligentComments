package com.intelligentcomments.ui.comments.renderers

import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentcomments.ui.core.RectangleModelBuildContext
import com.intelligentcomments.ui.core.RectangleModelBuildContributor
import com.intelligentcomments.ui.core.RectanglesModel
import com.intelligentcomments.ui.util.ContentSegmentsUtil
import com.intelligentcomments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

open class ContentSegmentsRenderer : SegmentRenderer, RectangleModelBuildContributor {
  private val segments: Collection<ContentSegmentUiModel>


  constructor(segments: Collection<ContentSegmentUiModel>) {
    this.segments = segments
  }

  constructor(segments: ContentSegmentsUiModel) : this(segments.contentSection.content)


  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    return ContentSegmentsUtil.renderSegments(segments, g, rect, editor, rectanglesModel)
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return ContentSegmentsUtil.calculateContentHeight(segments, editor, additionalRenderInfo)
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return ContentSegmentsUtil.calculateContentWidth(segments, editor, additionalRenderInfo)
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context.createCopy(), segments)
  }
}