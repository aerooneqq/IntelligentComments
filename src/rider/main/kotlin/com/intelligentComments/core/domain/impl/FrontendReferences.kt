package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.FrontendTicketReference
import com.intelligentComments.core.domain.core.TicketContentSegment
import com.intelligentComments.core.domain.core.UniqueEntityImpl

class FrontendTicketReferenceImpl(
  override val rawValue: String,
  override val model: TicketContentSegment
) : UniqueEntityImpl(), FrontendTicketReference