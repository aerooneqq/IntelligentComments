package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer

class RemarksSegmentRenderer(
  remarksUiModel: RemarksUiModel
) : ContentSegmentsRenderer(remarksUiModel.content), SegmentRenderer