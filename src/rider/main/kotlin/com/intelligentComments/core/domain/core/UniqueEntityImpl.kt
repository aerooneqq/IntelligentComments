package com.intelligentComments.core.domain.core

import java.util.*

interface UniqueEntity {
    val id: UUID
}

open class UniqueEntityImpl : UniqueEntity {
    override val id: UUID = UUID.randomUUID()
}