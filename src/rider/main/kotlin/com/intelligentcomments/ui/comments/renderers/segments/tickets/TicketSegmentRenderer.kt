package com.intelligentcomments.ui.comments.renderers.segments.tickets

import com.intelligentcomments.ui.comments.model.content.tickets.TicketUiModel
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class TicketSegmentRenderer(model: TicketUiModel) : LeftTextHeaderAndRightContentRenderer(
  model.displayName,
  model.description
)