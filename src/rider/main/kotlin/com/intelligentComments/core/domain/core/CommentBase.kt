package com.intelligentComments.core.domain.core

import com.intellij.openapi.editor.markup.RangeHighlighter

interface CommentBase : UniqueEntity {
  val highlighter: RangeHighlighter
  val commentIdentifier: Int
}