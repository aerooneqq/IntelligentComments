package com.intelligentComments.core.changes

import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intelligentComments.core.domain.core.HighlightedText
import java.util.UUID

interface RenderAffectedCommentChange {
  val id: CommentIdentifier
}

open class CommentChange(val id: CommentIdentifier) : Change

class CodeHighlightersChange(
  id: CommentIdentifier,
  val uuid: UUID,
  val newText: HighlightedText
) : RenderAffectedCommentChange, CommentChange(id)