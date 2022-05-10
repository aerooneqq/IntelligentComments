package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer

class ExampleSegmentRenderer(
  model: ExampleSegmentUiModel
) : ContentSegmentsRenderer(model.content.contentSection.content), SegmentRenderer