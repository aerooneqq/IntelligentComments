package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.hacks.InlineHackContentSegmentUiModel

class HackTextSegmentRenderer(
  private val model: InlineHackContentSegmentUiModel
) : LeftTextHeaderAndRightContentRenderer(
  model.header,
  model.content
)