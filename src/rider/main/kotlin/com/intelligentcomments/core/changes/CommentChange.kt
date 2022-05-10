package com.intelligentcomments.core.changes

import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.core.settings.CommentsDisplayKind
import java.util.*

interface RenderAffectedCommentChange {
  val id: CommentIdentifier
}

open class CommentChange(val id: CommentIdentifier) : Change

class CodeHighlightersChange(
  id: CommentIdentifier,
  val uuid: UUID,
  val newText: HighlightedText
) : RenderAffectedCommentChange, CommentChange(id)

class CommentDisplayKindChange(
  id: CommentIdentifier,
  newDisplayKind: CommentsDisplayKind,
) : RenderAffectedCommentChange, CommentChange(id)