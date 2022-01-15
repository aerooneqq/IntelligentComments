package com.intelligentComments.core.domain.core

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker


interface CommentBase : UniqueEntity, Parentable {
  val rangeMarker: RangeMarker
  val commentIdentifier: CommentIdentifier

  fun isValid(): Boolean {
    return rangeMarker.isValid
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