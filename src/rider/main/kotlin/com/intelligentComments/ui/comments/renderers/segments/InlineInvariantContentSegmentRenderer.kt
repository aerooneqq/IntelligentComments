package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.InlineContentSegmentUiModel

class InlineContentSegmentRenderer(
  model: InlineContentSegmentUiModel
) : LeftTextHeaderAndRightContentRenderer(
  model.header,
  model.content
)