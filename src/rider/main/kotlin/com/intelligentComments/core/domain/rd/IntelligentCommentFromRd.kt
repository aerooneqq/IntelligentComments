package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.IntelligentComment
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdIntelligentComment

class IntelligentCommentFromRd(
  private val rdComment: RdIntelligentComment,
  private val project: Project,
  rangeMarker: RangeMarker,
  highlighter: RangeHighlighter,
) : CommentFromRd(project, rangeMarker, highlighter), IntelligentComment {

  override val content = createContent(project)
  override val references = createReferences()
  override val invariants = createInvariants()
  override val hacks = createHacks()
  override val todos = createToDos()


  private fun createContent(project: Project) = IntelligentCommentContentFromRd(rdComment.content!!, this, project)
  private fun createReferences() = rdComment.references?.map { ReferenceContentSegmentFromRd(it, this, project) } ?: emptyList()
  private fun createInvariants() = rdComment.invariants?.map { InvariantContentSegmentFromRd(it, this, project) } ?: emptyList()
  private fun createToDos() = rdComment.toDos?.map { ToDoContentSegmentFromRd(it, this, project) } ?: emptyList()
  private fun createHacks() = rdComment.hacks?.map { HackWithTicketsContentSegmentFromRd(it, this, project) } ?: emptyList()


  override fun recreate(editor: Editor): CommentBase {
    TODO("Not yet implemented")
  }
}