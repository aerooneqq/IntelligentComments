package com.intelligentComments.core.comments.storages

import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intellij.openapi.editor.RangeMarker
import java.util.*
import kotlin.math.abs


class CommentsIdentifierStorage<T> {
  private val markersStorage = HashMap<RangeMarker, T>()
  private val idStorage = TreeMap<CommentIdentifier, T>()


  fun findNearestLeftToOffset(offset: Int): T? {
    val pairs = idStorage.entries.map { Pair(it.key, it.key.rangeMarker.endOffset) }
    var index = abs(pairs.map { it.second }.binarySearch(offset))
    if (index >= pairs.size || pairs[index].second != offset) {
      index -= 2
    }

    if (index < 0) return null

    return markersStorage[pairs[index].first.rangeMarker]
  }

  fun remove(commentIdentifier: CommentIdentifier) {
    idStorage.remove(commentIdentifier)
    markersStorage.remove(commentIdentifier.rangeMarker)
  }

  fun clear() {
    markersStorage.clear()
    idStorage.clear()
  }

  fun get(commentIdentifier: CommentIdentifier) = markersStorage[commentIdentifier.rangeMarker]

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
    markersStorage[key.rangeMarker] = value
    idStorage[key] = value
  }

  fun getAllKeysAndValues(): Collection<Pair<RangeMarker, T>> {
    return markersStorage.toList()
  }
}


