package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer

class ExampleSegmentRenderer(
  model: ExampleSegmentUiModel
) : ContentSegmentsRenderer(model.content.contentSection.content), SegmentRenderer