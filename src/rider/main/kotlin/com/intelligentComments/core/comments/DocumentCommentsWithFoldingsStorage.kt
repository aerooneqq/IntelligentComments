package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rd.util.reactive.ViewableMap

class DocumentCommentsWithFoldingsStorage : DocumentCommentsStorage() {
  private val foldings = ViewableMap<CommentIdentifier, ViewableMap<Editor, CustomFoldRegion>>()


  fun addFoldingToComment(
    comment: CommentBase,
    folding: CustomFoldRegion,
    editor: Editor
  ) {
    val editorsFoldings = foldings.getOrCreate(comment.commentIdentifier) { ViewableMap() }
    editorsFoldings[editor] = folding
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): CustomFoldRegion? {
    return foldings[commentIdentifier]?.get(editor)
  }

  fun removeFolding(commentIdentifier: CommentIdentifier, editor: Editor) {
    foldings[commentIdentifier]?.remove(editor)
  }
}