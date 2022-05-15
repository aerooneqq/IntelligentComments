package com.intelligentcomments.core.comments.storages

import com.intelligentcomments.core.domain.core.CommentBase
import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.platform.util.getLogger

open class EditorCommentsStorage {
  companion object {
    private val logger = getLogger<EditorCommentsStorage>()
  }

  private val syncObject = Any()
  private val comments = HashMap<Editor, CommentsIdentifierStorage<CommentBase>>()


  fun findNearestLeftCommentTo(editor: Editor, offset: Int): CommentBase? {
    synchronized(syncObject) {
      val editorComments = comments[editor] ?: return null
      return editorComments.findNearestLeftToOffset(offset)
    }
  }

  fun findNearestCommentTo(editor: Editor, offset: Int): CommentBase? {
    synchronized(syncObject) {
      val editorComments = comments[editor] ?: return null
      return editorComments.findNearestToOffset(offset)
    }
  }

  fun recreateAllCommentsFor(editor: Editor) {
    synchronized(syncObject) {
      val storage = comments[editor] ?: return
      val allComments = storage.getAllKeysAndValues().map { it.second.recreate(editor) }
      storage.clear()
      for (comment in allComments) {
        storage.add(comment.identifier, comment)
      }
    }
  }

  fun getAllComments(editor: Editor): Collection<CommentBase> {
    synchronized(syncObject) {
      return comments[editor]?.getAllKeysAndValues()?.map { it.second } ?: emptyList()
    }
  }

  fun getComment(commentIdentifier: CommentIdentifier, editor: Editor): CommentBase? {
    synchronized(syncObject) {
      val editorComments = comments[editor]
      val comment = editorComments?.getWithAdditionalSearch(commentIdentifier)

      if (comment == null){
        logger.error("Comment for given ID $commentIdentifier does not exist")
      }

      return comment
    }
  }

  fun addNewComment(comment: CommentBase, editor: Editor) {
    synchronized(syncObject) {
      val editorComments = if (editor !in comments) {
        val storage = CommentsIdentifierStorage<CommentBase>()
        comments[editor] = storage
        storage
      } else {
        comments[editor] ?: return
      }

      editorComments.add(comment.identifier, comment)
    }
  }
}