package com.intelligentComments.ui.comments.renderers.segments.tickets

import com.intelligentComments.ui.comments.model.content.tickets.GroupedTicketsUiModel
import com.intelligentComments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class GroupedTicketsSegmentRenderer(
  model: GroupedTicketsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)