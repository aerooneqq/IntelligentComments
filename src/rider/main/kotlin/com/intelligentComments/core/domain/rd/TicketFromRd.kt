package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.Ticket
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.jetbrains.rd.ide.model.RdTicket

class TicketFromRd(rdTicket: RdTicket) : UniqueEntityImpl(), Ticket {
    override val url: String = rdTicket.url
    override val shortName: String = rdTicket.shortName
}