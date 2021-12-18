package com.intelligentComments.core.utils

import com.intellij.openapi.editor.RangeMarker

fun RangeMarker.toGreedy() {
  isGreedyToRight = true
  isGreedyToLeft = true
}