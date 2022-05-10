package com.intelligentcomments.ui.comments.renderers.segments.invariants

import com.intelligentcomments.ui.comments.model.content.invariants.GroupedInvariantsUiModel
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class GroupedInvariantsRenderer(
  model: GroupedInvariantsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)