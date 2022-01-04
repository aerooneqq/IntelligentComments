package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rd.util.reactive.ViewableMap
import java.util.*

class DocumentCommentsWithFoldingsStorage : DocumentCommentsStorage() {
  private val foldings = TreeMap<CommentIdentifier, ViewableMap<Editor, CustomFoldRegion>>()
  private val foldingsPerEditor = ViewableMap<Editor, MutableList<CustomFoldRegion>>()


  fun addFoldingToComment(
    comment: CommentBase,
    folding: CustomFoldRegion,
    editor: Editor
  ) {
    val editorsFoldings = foldings.getOrCreate(comment.commentIdentifier) { ViewableMap() }
    editorsFoldings[editor] = folding
    val foldingsList = foldingsPerEditor.getOrCreate(editor) { mutableListOf() }
    foldingsList.add(folding)
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): CustomFoldRegion? {
    return foldings[commentIdentifier]?.get(editor)
  }

  fun removeFolding(commentIdentifier: CommentIdentifier, editor: Editor) {
    val folding = foldings[commentIdentifier]?.remove(editor)

    if (folding != null) {
      foldingsPerEditor[editor]?.remove(folding)
    }
  }

  fun getAllFoldingsFor(editor: Editor): Collection<CustomFoldRegion> {
    return foldingsPerEditor[editor] ?: emptyList()
  }
}