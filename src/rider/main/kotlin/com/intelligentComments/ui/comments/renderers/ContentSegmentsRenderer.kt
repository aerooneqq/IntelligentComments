package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

open class ContentSegmentsRenderer(private val segments: Collection<ContentSegmentUiModel>) : Renderer,
  RectangleModelBuildContributor {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    return ContentSegmentsUtil.renderSegments(segments, g, rect, editorImpl, rectanglesModel)
  }

  override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
    return ContentSegmentsUtil.calculateContentHeight(segments, editorImpl)
  }

  override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
    return ContentSegmentsUtil.calculateContentWidth(segments, editorImpl)
  }

  override fun accept(context: RectangleModelBuildContext) {
    ContentSegmentsUtil.accept(context, segments)
  }
}