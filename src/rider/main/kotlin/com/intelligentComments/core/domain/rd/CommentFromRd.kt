package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.jetbrains.rd.ide.model.RdComment
import com.jetbrains.rd.ide.model.RdDocComment
import com.jetbrains.rd.ide.model.RdIntelligentComment

open class CommentFromRd(rdComment: RdComment,
                         final override val highlighter: RangeHighlighter) : UniqueEntityImpl(), CommentBase {
  companion object {
    fun getFor(rdComment: RdComment, project: Project, highlighter: RangeHighlighter): CommentBase {
      return when(rdComment) {
        is RdDocComment -> DocCommentFromRd(rdComment, project, highlighter)
        is RdIntelligentComment -> IntelligentCommentFromRd(rdComment, project, highlighter)
        else -> throw IllegalArgumentException(rdComment.javaClass.name)
      }
    }
  }

  final override val commentIdentifier: Int = rdComment.commentIdentifier
}