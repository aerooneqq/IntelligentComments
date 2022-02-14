package com.intelligentComments.core.domain.core

interface IntelligentComment : CommentBase {
  val content: IntelligentCommentContent
  val references: Collection<ReferenceContentSegment>
  val invariants: Collection<TextInvariantContentSegment>
  val todos: Collection<ToDoWithTicketsContentSegment>
  val hacks: Collection<HackWithTicketsContentSegment>
}