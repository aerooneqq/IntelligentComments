package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.util.containers.BidirectionalMap
import java.util.*


class CommentsIdentifierStorage<T> {
  private val storage = TreeMap<CommentIdentifier, T>()
  private val startOffsetsToComments = BidirectionalMap<Int, CommentIdentifier>()
  private val endOffsetsToComments = BidirectionalMap<Int, CommentIdentifier>()


  fun get(commentIdentifier: CommentIdentifier) = storage[commentIdentifier]

  fun getWithAdditionalSearch(commentIdentifier: CommentIdentifier): T? {
    val existingValue = get(commentIdentifier)
    if (existingValue != null) return existingValue

    var id = startOffsetsToComments[commentIdentifier.rangeMarker.startOffset]

    if (id != null) {
      return storage[id]
    }

    id = startOffsetsToComments[commentIdentifier.rangeMarker.endOffset]

    if (id != null) {
      return storage[id]
    }

    return null
  }

  fun getOrCreate(commentIdentifier: CommentIdentifier, creator: (CommentIdentifier) -> T): T {
    val existingValue = get(commentIdentifier)
    if (existingValue != null) return existingValue

    val createdValue = creator(commentIdentifier)
    add(commentIdentifier, createdValue)
    return createdValue
  }

  fun add(commentIdentifier: CommentIdentifier, value: T) {
    val startOffset = commentIdentifier.rangeMarker.startOffset
    val endOffset = commentIdentifier.rangeMarker.endOffset

    if (commentIdentifier in storage) {
      addNewCommentInternal(commentIdentifier, value)
    } else {
      var oldId: CommentIdentifier? = null
      if (startOffset in startOffsetsToComments) {
        oldId = startOffsetsToComments[startOffset]
      } else if (endOffset in endOffsetsToComments) {
        oldId = endOffsetsToComments[endOffset]
      }

      if (oldId != null) {
        addNewCommentInternal(oldId, value)
      } else {
        addNewCommentInternal(commentIdentifier, value)
      }
    }
  }

  private fun addNewCommentInternal(
    key: CommentIdentifier,
    value: T
  ) {
    startOffsetsToComments.removeValue(key)
    endOffsetsToComments.removeValue(key)

    storage[key] = value
    startOffsetsToComments[key.rangeMarker.startOffset] = key
    endOffsetsToComments[key.rangeMarker.endOffset] = key
  }

  fun getAllKeysAndValues(): Collection<Pair<CommentIdentifier, T>> {
    return storage.toList()
  }
}


