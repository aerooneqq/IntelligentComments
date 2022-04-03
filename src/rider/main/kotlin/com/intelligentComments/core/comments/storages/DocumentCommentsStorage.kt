package com.intelligentComments.core.comments.storages

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.platform.util.getLogger

open class DocumentCommentsStorage {
  companion object {
    private val logger = getLogger<DocumentCommentsStorage>()
  }

  private val syncObject = Any()
  private val comments = HashMap<Document, CommentsIdentifierStorage<CommentBase>>()


  fun findNearestLeftCommentTo(editor: Editor, offset: Int): CommentBase? {
    synchronized(syncObject) {
      val documentComments = comments[editor.document] ?: return null
      return documentComments.findNearestLeftToOffset(offset)
    }
  }

  fun findNearestCommentTo(editor: Editor, offset: Int): CommentBase? {
    synchronized(syncObject) {
      val documentComments = comments[editor.document] ?: return null
      return documentComments.findNearestToOffset(offset)
    }
  }

  fun recreateAllCommentsFor(editor: Editor) {
    synchronized(syncObject) {
      val storage = comments[editor.document] ?: return
      val allComments = storage.getAllKeysAndValues().map { it.second.recreate(editor) }
      storage.clear()
      for (comment in allComments) {
        storage.add(comment.identifier, comment)
      }
    }
  }

  fun getAllComments(editor: Editor): Collection<CommentBase> {
    synchronized(syncObject) {
      return comments[editor.document]?.getAllKeysAndValues()?.map { it.second } ?: emptyList()
    }
  }

  fun getComment(commentIdentifier: CommentIdentifier, document: Document): CommentBase? {
    synchronized(syncObject) {
      val documentComments = comments[document]
      val comment = documentComments?.getWithAdditionalSearch(commentIdentifier)

      if (comment == null){
        logger.error("Comment for given ID $commentIdentifier does not exist")
      }

      return comment
    }
  }

  fun addNewComment(comment: CommentBase, editor: Editor) {
    synchronized(syncObject) {
      val document = editor.document
      val documentComments = if (document !in comments) {
        val storage = CommentsIdentifierStorage<CommentBase>()
        comments[document] = storage
        storage
      } else {
        comments[document] ?: return
      }

      documentComments.add(comment.identifier, comment)
    }
  }
}