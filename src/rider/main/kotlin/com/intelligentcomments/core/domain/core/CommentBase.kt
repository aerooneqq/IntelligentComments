package com.intelligentcomments.core.domain.core

import com.intelligentcomments.ui.comments.model.CommentUiModelBase
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter


interface CommentBase : UniqueEntity, Parentable {
  val correspondingHighlighter: RangeHighlighter
  val identifier: CommentIdentifier
  val uiModel: CommentUiModelBase

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
interface InlineReferenceComment : CommentWithOneTextSegment

interface CommentWithOneContentSegments : CommentBase {
  val content: ContentSegments
}

interface InlineComment : CommentWithOneContentSegments
interface ToDoInlineComment : InlineComment
interface HackInlineComment : InlineComment
interface InvariantInlineComment : InlineComment