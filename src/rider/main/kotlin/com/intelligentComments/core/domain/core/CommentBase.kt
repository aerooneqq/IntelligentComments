package com.intelligentComments.core.domain.core

import com.intellij.openapi.util.TextRange
import com.intellij.util.Range

interface CommentBase : UniqueEntity {
    val underlyingTextRange: TextRange
}