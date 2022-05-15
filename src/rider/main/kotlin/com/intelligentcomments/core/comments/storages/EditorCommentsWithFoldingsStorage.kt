package com.intelligentcomments.core.comments.storages

import com.intelligentcomments.core.domain.core.CommentBase
import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.FoldRegion
import com.jetbrains.rd.util.getOrCreate
import java.util.*

class EditorCommentsWithFoldingsStorage : EditorCommentsStorage() {
  private val foldings = HashMap<Editor, TreeMap<CommentIdentifier, FoldRegion>>()


  fun addFoldingToComment(
    comment: CommentBase,
    folding: FoldRegion,
    editor: Editor
  ) {
    val editorFoldings = foldings.getOrCreate(editor) { TreeMap() }
    editorFoldings[comment.identifier] = folding
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): FoldRegion? {
    return foldings[editor]?.get(commentIdentifier)
  }

  fun removeFolding(commentIdentifier: CommentIdentifier, editor: Editor) {
    foldings[editor]?.remove(commentIdentifier)
  }

  fun getAllFoldingsFor(editor: Editor): Collection<FoldRegion> {
    return foldings[editor]?.values ?: emptyList()
  }
}