package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.renderers.ContentSegmentsRenderer

class RemarksSegmentRenderer(
  remarksUiModel: RemarksUiModel
) : ContentSegmentsRenderer(remarksUiModel.content), SegmentRenderer