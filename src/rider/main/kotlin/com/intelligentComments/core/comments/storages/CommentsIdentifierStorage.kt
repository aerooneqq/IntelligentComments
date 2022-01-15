package com.intelligentComments.core.comments.storages

import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.RangeMarker


class CommentsIdentifierStorage<T> {
  private val storage = HashMap<RangeMarker, T>()


  fun remove(commentIdentifier: CommentIdentifier) {
    storage.remove(commentIdentifier.rangeMarker)
  }

  fun clear() {
    storage.clear()
  }

  fun get(commentIdentifier: CommentIdentifier) = storage[commentIdentifier.rangeMarker]

  fun getWithAdditionalSearch(commentIdentifier: CommentIdentifier): T? {
    return get(commentIdentifier)
  }

  fun getOrCreate(commentIdentifier: CommentIdentifier, creator: (CommentIdentifier) -> T): T {
    val existingValue = get(commentIdentifier)
    if (existingValue != null) return existingValue

    val createdValue = creator(commentIdentifier)
    add(commentIdentifier, createdValue)
    return createdValue
  }

  fun add(commentIdentifier: CommentIdentifier, value: T) {
    addNewCommentInternal(commentIdentifier.rangeMarker, value)
  }

  private fun addNewCommentInternal(
    key: RangeMarker,
    value: T
  ) {
    storage[key] = value
  }

  fun getAllKeysAndValues(): Collection<Pair<RangeMarker, T>> {
    return storage.toList()
  }
}


