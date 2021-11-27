package com.intelligentComments.ui.util

import java.lang.Integer.max

class WidthAndHeight {
  private var myWidth = 0
  private var myHeight = 0
  private var myAdditionalWidth = 0

  val width
    get() = myWidth

  val height
    get() = myHeight


  fun updateWidthMax(newWidth: Int) {
    myWidth = max(myWidth, newWidth + myAdditionalWidth)
  }

  fun updateWidthSum(newWidth: Int) {
    myWidth += newWidth + myAdditionalWidth
  }

  fun updateHeightSum(newHeight: Int) {
    myHeight += newHeight
  }

  fun executeWithAdditionalWidth(additionalWidth: Int, action: () -> Unit) {
    myAdditionalWidth += additionalWidth
    action()
    myAdditionalWidth -= additionalWidth
  }
}