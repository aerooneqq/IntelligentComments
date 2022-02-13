package com.intelligentComments.core.comments.states

import com.intelligentComments.core.settings.CommentsDisplayKind

class CommentState {
  companion object {
    val defaultInstance = CommentState()
  }


  constructor()

  constructor(displayKind: CommentsDisplayKind) {
    this.displayKind = displayKind
  }

  constructor(snapshot: CommentStateSnapshot) {
    displayKind = snapshot.displayKind
    lastRelativeCaretOffsetWithinComment = snapshot.lastRelativeCaretPositionWithinComment
  }


  var displayKind = CommentsDisplayKind.Render
    private set

  var lastRelativeCaretOffsetWithinComment = 0
  val isInRenderMode: Boolean
    get() = displayKind != CommentsDisplayKind.Code

  fun setDisplayKind(displayKind: CommentsDisplayKind) {
    this.displayKind = displayKind
  }
}