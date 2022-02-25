package com.intelligentComments.ui.comments.renderers.segments.invariants

import com.intelligentComments.ui.comments.model.content.invariants.GroupedInvariantsUiModel
import com.intelligentComments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class GroupedInvariantsRenderer(
  model: GroupedInvariantsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)