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

  private fun invalidate(editor: Editor) {
    val map = foldings[editor] ?: return

    val idsToRemove = mutableSetOf<CommentIdentifier>()
    for ((id, region) in map) {
      if (!region.isValid) {
        idsToRemove.add(id)
      }
    }

    for (id in idsToRemove) {
      map.remove(id)
    }
  }

  fun getFolding(commentIdentifier: CommentIdentifier, editor: Editor): FoldRegion? {
    val map = foldings[editor] ?: return null

    val folding = map[commentIdentifier]
    if (folding != null && !folding.isValid) {
      map.remove(commentIdentifier)
      return null
    }

    return folding
  }

  fun removeFolding(commentIdentifier: CommentIdentifier, editor: Editor) {
    foldings[editor]?.remove(commentIdentifier)
  }

  fun getAllFoldingsFor(editor: Editor): Collection<FoldRegion> {
    invalidate(editor)
    return foldings[editor]?.values ?: emptyList()
  }
}