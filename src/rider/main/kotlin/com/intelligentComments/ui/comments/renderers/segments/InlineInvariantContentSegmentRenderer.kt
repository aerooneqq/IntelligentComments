package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.invariants.InlineInvariantContentSegmentUiModel

class InlineInvariantContentSegmentRenderer(
  private val model: InlineInvariantContentSegmentUiModel
) : LeftTextHeaderAndRightContentRenderer(
  model.header,
  model.content
)