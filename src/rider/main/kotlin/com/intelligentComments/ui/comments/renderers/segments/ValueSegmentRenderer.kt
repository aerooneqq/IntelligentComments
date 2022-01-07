package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.value.ValueUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer

class ValueSegmentRenderer(
  model: ValueUiModel
) : ContentSegmentsRenderer(model.content.content), SegmentRenderer