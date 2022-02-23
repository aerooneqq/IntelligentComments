package com.intelligentComments.core.domain.core

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter


interface CommentBase : UniqueEntity, Parentable {
  val correspondingHighlighter: RangeHighlighter
  val identifier: CommentIdentifier

  fun isValid(): Boolean {
    return identifier.isValid
  }

  fun recreate(editor: Editor): CommentBase
}

interface DocComment : CommentBase {
  val content: IntelligentCommentContent
}

interface CommentWithOneTextSegment : CommentBase {
  val text: TextContentSegment
}

interface GroupOfLineComments : CommentWithOneTextSegment
interface MultilineComment : CommentWithOneTextSegment
interface InvalidComment : CommentWithOneTextSegment
interface DisablingInspectionsComment : CommentWithOneTextSegment