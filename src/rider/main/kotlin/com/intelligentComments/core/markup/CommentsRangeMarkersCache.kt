package com.intelligentComments.core.markup

import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.util.TextRange
import com.intellij.util.application

class CommentsRangeMarkersCache {
  private data class RangeMarkerWithStamp(val rangeMarker: RangeMarker, val stamp: Int)


  private var myStamp: Int = 0

  private val syncObject = Any()
  private val itemLifeTime = 5
  private val cachedRangesInfos = mutableListOf<RangeMarkerWithStamp>()


  fun store(rangeMarker: RangeMarker) {
    synchronized(syncObject) {
      cachedRangesInfos.add(RangeMarkerWithStamp(rangeMarker, myStamp))
    }
  }

  fun invalidateAndSort() {
    application.assertIsNonDispatchThread()
    synchronized(syncObject) {
      invalidate()
      cachedRangesInfos.sortBy { it.rangeMarker.startOffset }
    }
  }

  fun tryGetFor(textRange: TextRange): RangeMarker? {
    synchronized(syncObject) {
      val index = cachedRangesInfos.binarySearch { it.rangeMarker.startOffset - textRange.startOffset }
      if (index >= 0) {
        val marker = cachedRangesInfos[index].rangeMarker
        cachedRangesInfos.removeAt(index)

        if (!marker.isValid || marker.endOffset != textRange.endOffset) return null
        return marker
      }

      return null
    }
  }

  private fun invalidate() {
    ++myStamp
    val itemsToRemove = mutableListOf<Int>()
    val seenStartRanges = hashSetOf<Int>()

    for (index in cachedRangesInfos.indices) {
      val info = cachedRangesInfos[index]
      val startOffset = info.rangeMarker.startOffset
      if (seenStartRanges.contains(startOffset)) {
        itemsToRemove.add(index)
        continue
      }

      seenStartRanges.add(startOffset)
      if (!info.rangeMarker.isValid || info.stamp - myStamp > itemLifeTime) {
        itemsToRemove.add(index)
      }
    }

    for (index in itemsToRemove.reversed()) {
      cachedRangesInfos.removeAt(index)
    }
  }
}