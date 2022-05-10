package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.value.ValueUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer

class ValueSegmentRenderer(
  model: ValueUiModel
) : ContentSegmentsRenderer(model.content), SegmentRenderer