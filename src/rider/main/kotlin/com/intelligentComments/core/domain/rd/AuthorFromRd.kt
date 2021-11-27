package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.CommentAuthor
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.jetbrains.rd.ide.model.RdIntelligentCommentAuthor
import java.util.*

class AuthorFromRd(author: RdIntelligentCommentAuthor) : UniqueEntityImpl(), CommentAuthor {
  override val name: String = author.name
  override val date: Date = author.date
}