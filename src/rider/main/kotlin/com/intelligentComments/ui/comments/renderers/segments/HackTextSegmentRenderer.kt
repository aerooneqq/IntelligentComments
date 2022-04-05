package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.hacks.HackTextContentSegmentUiModel

class HackTextSegmentRenderer(
  private val model: HackTextContentSegmentUiModel
) : LeftTextHeaderAndRightContentRenderer(
  model.header,
  model.content
)