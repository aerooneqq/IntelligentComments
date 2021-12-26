package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext

class ExampleSegmentRenderer(
  model: ExampleSegmentUiModel
) : ContentSegmentsRenderer(model.content.content), SegmentRenderer {
  override fun accept(context: RectangleModelBuildContext) {
  }
}