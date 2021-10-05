package com.intelligentComments.core.domain.core

import java.util.*

interface CommentAuthor : UniqueEntity {
    val name: String
    val date: Date
}