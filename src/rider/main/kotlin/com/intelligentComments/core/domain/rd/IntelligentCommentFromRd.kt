package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.IntelligentCommentsRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdIntelligentComment

class IntelligentCommentFromRd(
  private val rdComment: RdIntelligentComment,
  private val project: Project,
  highlighter: RangeMarker
) : CommentFromRd(rdComment, project, highlighter), IntelligentComment {

  override val allAuthors: Collection<CommentAuthor> = createAuthors()
  override val content: IntelligentCommentContent = createContent(project)
  override val references: Collection<Reference> = createReferences()
  override val invariants: Collection<Invariant> = createInvariants()
  override val hacks: Collection<Hack> = createHacks()
  override val todos: Collection<ToDo> = createToDos()


  private fun createAuthors(): List<AuthorFromRd> {
    return rdComment.authors?.map { AuthorFromRd(it) } ?: emptyList()
  }

  private fun createContent(project: Project) = IntelligentCommentContentFromRd(rdComment.content, this, project)
  private fun createReferences(): List<ReferenceFromRd> =
    rdComment.references?.map { ReferenceFromRd.getFrom(project, it) } ?: emptyList()

  private fun createInvariants(): List<InvariantFromRd> =
    rdComment.invariants?.map { InvariantFromRd.getFrom(it) } ?: emptyList()

  private fun createToDos(): List<ToDoFromRd> = rdComment.toDos?.map { ToDoFromRd.getFrom(it, project) } ?: emptyList()
  private fun createHacks(): List<HackFromRd> = rdComment.hacks?.map { HackFromRd.getFrom(it, project) } ?: emptyList()

  fun getRenderer(project: Project): EditorCustomElementRenderer {
    return IntelligentCommentsRenderer(IntelligentCommentUiModel(project, this))
  }

  override fun isValid(): Boolean {
    return super.isValid()
  }

  override fun recreate(editor: Editor): CommentBase {
    TODO("Not yet implemented")
  }
}