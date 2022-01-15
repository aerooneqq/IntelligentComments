package com.intelligentComments.core.markup

import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.tree.injected.changesHandler.range
import com.intellij.util.application

class CommentsRangeMarkersCache {
  data class RangeMarkerWithStamp(val rangeMarker: RangeMarker, val stamp: Int)


  private var myStamp: Int = 0

  private val itemLifeTime = 5
  private val cachedDeletedCommentsRanges = HashMap<Int, RangeMarkerWithStamp>()


  fun store(rangeMarker: RangeMarker) {
    application.assertIsDispatchThread()

    cachedDeletedCommentsRanges[rangeMarker.range.hashCode()] = RangeMarkerWithStamp(rangeMarker, myStamp)
  }

  fun tryGetFor(textRange: TextRange): RangeMarker? {
    application.assertIsDispatchThread()

    val markerInfo = cachedDeletedCommentsRanges.remove(textRange.hashCode())
    if (markerInfo?.rangeMarker?.isValid != true) return null
    return markerInfo.rangeMarker
  }

  fun invalidate() {
    application.assertIsDispatchThread()

    ++myStamp
    val itemsToRemove = mutableSetOf<Int>()
    for ((hash, info) in cachedDeletedCommentsRanges) {
      if (!info.rangeMarker.isValid || info.stamp - myStamp > itemLifeTime) {
        itemsToRemove.add(hash)
      }
    }

    for (hash in itemsToRemove) {
      cachedDeletedCommentsRanges.remove(hash)
    }
  }
}