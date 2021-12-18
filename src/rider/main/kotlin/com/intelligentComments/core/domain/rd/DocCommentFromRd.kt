package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdDocComment

class DocCommentFromRd(
  rdDocComment: RdDocComment,
  project: Project,
  range: RangeHighlighter
) : CommentFromRd(rdDocComment, project, range), DocComment {
  override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(rdDocComment.content, project)
}