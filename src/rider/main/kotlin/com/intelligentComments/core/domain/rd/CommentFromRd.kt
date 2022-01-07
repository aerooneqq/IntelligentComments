package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intelligentComments.core.domain.core.Parentable
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdComment
import com.jetbrains.rd.ide.model.RdDocComment
import com.jetbrains.rd.ide.model.RdIntelligentComment

abstract class CommentFromRd(
  private val rdComment: RdComment,
  private val project: Project,
  final override val rangeMarker: RangeMarker
) : UniqueEntityImpl(), CommentBase {
  companion object {
    fun getFor(rdComment: RdComment, project: Project, highlighter: RangeMarker): CommentBase {
      return when (rdComment) {
        is RdDocComment -> DocCommentFromRd(rdComment, project, highlighter)
        is RdIntelligentComment -> IntelligentCommentFromRd(rdComment, project, highlighter)
        else -> throw IllegalArgumentException(rdComment.javaClass.name)
      }
    }
  }

  final override val parent: Parentable? = null

  final override val commentIdentifier: CommentIdentifier =
    CommentIdentifier.create(rangeMarker.document, project, rangeMarker)

  abstract override fun isValid(): Boolean
  abstract override fun recreate(editor: Editor): CommentBase
}