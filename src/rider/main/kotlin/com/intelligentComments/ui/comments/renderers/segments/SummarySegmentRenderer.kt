package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.summary.SummaryUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer

class SummarySegmentRenderer(
  model: SummaryUiModel
) : ContentSegmentsRenderer(model.content), SegmentRenderer