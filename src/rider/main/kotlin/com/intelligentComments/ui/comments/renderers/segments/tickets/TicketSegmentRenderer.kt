package com.intelligentComments.ui.comments.renderers.segments.tickets

import com.intelligentComments.ui.comments.model.content.tickets.TicketUiModel
import com.intelligentComments.ui.comments.renderers.segments.LeftHeaderRightContentRenderer
import com.intelligentComments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentComments.ui.comments.renderers.segments.TextRendererBase

class TicketSegmentRenderer(model: TicketUiModel) : LeftTextHeaderAndRightContentRenderer(
  model.displayName,
  model.description
)