package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.summary.SummaryUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer

class SummarySegmentRenderer(
  model: SummaryUiModel
) : ContentSegmentsRenderer(model.content), SegmentRenderer