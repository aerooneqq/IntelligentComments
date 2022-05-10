package com.intelligentcomments.core.domain.impl

import com.intelligentcomments.core.domain.core.FrontendTicketReference
import com.intelligentcomments.core.domain.core.TicketContentSegment
import com.intelligentcomments.core.domain.core.UniqueEntityImpl

class FrontendTicketReferenceImpl(
  override val rawValue: String,
  override val model: TicketContentSegment
) : UniqueEntityImpl(), FrontendTicketReference