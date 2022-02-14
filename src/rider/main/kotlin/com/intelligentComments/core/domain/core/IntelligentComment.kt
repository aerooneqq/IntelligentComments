package com.intelligentComments.core.domain.core

interface IntelligentComment : CommentBase {
  val content: IntelligentCommentContent
  val references: Collection<ReferenceContentSegment>
  val invariants: Collection<InvariantContentSegment>
  val todos: Collection<ToDoContentSegment>
  val hacks: Collection<HackContentSegment>
}