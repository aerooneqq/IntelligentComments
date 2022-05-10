package com.intelligentcomments.core.utils

import com.intellij.openapi.editor.RangeMarker

fun RangeMarker.toGreedy() {
  isGreedyToRight = true
  isGreedyToLeft = true
}