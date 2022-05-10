package com.intelligentcomments.core.comments.storages

import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.RangeMarker
import java.util.*
import kotlin.math.abs


class CommentsIdentifierStorage<T> {
  private val idStorage = TreeMap<CommentIdentifier, T>()


  fun findNearestLeftToOffset(offset: Int): T? {
    var (index, pairs) = find(offset)
    if (index < 0 || pairs[index].second != offset) {
      index = abs(index) - 2
    }

    if (index < 0 || index >= pairs.size) return null
    return idStorage[pairs[index].first]
  }

  private fun find(offset: Int): Pair<Int, List<Pair<CommentIdentifier, Int>>> {
    val pairs = idStorage.entries.map { Pair(it.key, it.key.rangeMarker.endOffset) }
    return Pair(pairs.map { it.second }.binarySearch(offset), pairs)
  }

  fun findNearestToOffset(offset: Int): T? {
    var (index, pairs) = find(offset)
    if (index < 0) {
      index = abs(index) - 1
    }

    if (index < 0 || index >= pairs.size) return null
    return idStorage[pairs[index].first]
  }

  fun remove(commentIdentifier: CommentIdentifier) {
    idStorage.remove(commentIdentifier)
  }

  fun clear() {
    idStorage.clear()
  }

  fun get(commentIdentifier: CommentIdentifier) = idStorage[commentIdentifier]

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
    addNewCommentInternal(commentIdentifier, value)
  }

  private fun addNewCommentInternal(
    key: CommentIdentifier,
    value: T
  ) {
    idStorage[key] = value
  }

  fun getAllKeysAndValues(): Collection<Pair<RangeMarker, T>> {
    return idStorage.map { Pair(it.key.rangeMarker, it.value) }.toList()
  }
}


