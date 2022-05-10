package com.intelligentcomments.ui.util

import com.intellij.openapi.Disposable
import java.awt.Rectangle

class UpdatedRectCookie(
  private val rect: Rectangle,
  private val xDelta: Int = 0,
  private val yDelta: Int = 0,
  private val widthDelta: Int = 0,
  private val heightDelta: Int = 0
) : Disposable {
  private val initialRect = Rectangle(rect)

  init {
    rect.apply {
      x += xDelta
      y += yDelta
      width += widthDelta
      height += heightDelta
    }
  }

  override fun dispose() {
    rect.apply {
      x = initialRect.x
      y = initialRect.y
      width = initialRect.width
      height = initialRect.height
    }
  }
}