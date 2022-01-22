package com.intelligentComments.core.comments.storages

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.util.getOrCreate
import java.util.*

class DocumentCommentsWithFoldingsStorage : DocumentCommentsStorage() {
  private val foldings = HashMap<Editor, TreeMap<CommentIdentifier, CustomFoldRegion>>()


  fun addFoldingToComment(
    comment: CommentBase,
    folding: CustomFoldRegion,
    editor: Editor
  ) {
    val editorFoldings = foldings.getOrCreate(editor) { TreeMap() }
    editorFoldings[comment.commentIdentifier] = folding
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): CustomFoldRegion? {
    return foldings[editor]?.get(commentIdentifier)
  }

  fun removeFolding(commentIdentifier: CommentIdentifier, editor: Editor) {
    foldings[editor]?.remove(commentIdentifier)
  }

  fun getAllFoldingsFor(editor: Editor): Collection<CustomFoldRegion> {
    return foldings[editor]?.values ?: emptyList()
  }
}