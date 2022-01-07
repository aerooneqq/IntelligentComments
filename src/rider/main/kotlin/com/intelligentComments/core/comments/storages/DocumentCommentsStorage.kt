package com.intelligentComments.core.comments.storages

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.platform.util.getLogger

open class DocumentCommentsStorage {
  private val logger = getLogger<DocumentCommentsStorage>()
  private val comments = HashMap<Document, CommentsIdentifierStorage<CommentBase>>()


  fun recreateAllCommentsFor(editor: Editor) {
    val storage = comments[editor.document] ?: return
    val allComments = storage.getAllKeysAndValues().map { it.second.recreate(editor) }
    storage.clear()
    for (comment in allComments) {
      storage.add(comment.commentIdentifier, comment)
    }
  }

  fun getAllComments(editor: Editor): Collection<CommentBase> {
    return comments[editor.document]?.getAllKeysAndValues()?.map { it.second } ?: emptyList()
  }

  fun getComment(commentIdentifier: CommentIdentifier, document: Document): CommentBase? {
    val documentComments = comments[document]
    val comment = documentComments?.getWithAdditionalSearch(commentIdentifier)

    if (comment == null){
      logger.error("Comment for given ID $commentIdentifier does not exist")
    }

    return comment
  }

  fun addNewComment(comment: CommentBase, editor: Editor) {
    val document = editor.document
    val documentComments = if (document !in comments) {
      val storage = CommentsIdentifierStorage<CommentBase>()
      comments[document] = storage
      storage
    } else {
      comments[document] ?: return
    }

    documentComments.add(comment.commentIdentifier, comment)
  }
}