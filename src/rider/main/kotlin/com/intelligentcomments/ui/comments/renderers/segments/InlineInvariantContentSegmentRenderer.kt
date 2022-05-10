package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.InlineContentSegmentUiModel

class InlineContentSegmentRenderer(
  model: InlineContentSegmentUiModel
) : LeftTextHeaderAndRightContentRenderer(
  model.header,
  model.content
)