package com.intelligentComments.core.comments.states

class CommentState {
  companion object {
    val defaultInstance = CommentState()
  }


  constructor()

  constructor(isInRenderMode: Boolean) {
    this.isInRenderMode = isInRenderMode
  }

  constructor(snapshot: CommentStateSnapshot) {
    isInRenderMode = snapshot.isInRenderMode
    lastRelativeCaretOffsetWithinComment = snapshot.lastRelativeCaretPositionWithinComment
  }


  var isInRenderMode = false
    private set

  var lastRelativeCaretOffsetWithinComment = 0


  fun changeRenderMode() {
    isInRenderMode = !isInRenderMode
  }
}