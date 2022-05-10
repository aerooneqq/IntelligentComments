package com.intelligentcomments.ui.comments.renderers.segments.tickets

import com.intelligentcomments.ui.comments.model.content.tickets.GroupedTicketsUiModel
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class GroupedTicketsSegmentRenderer(
  model: GroupedTicketsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)