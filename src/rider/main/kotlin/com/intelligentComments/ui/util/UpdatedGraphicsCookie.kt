package com.intelligentComments.ui.util

import com.intellij.openapi.Disposable
import java.awt.Color
import java.awt.Font
import java.awt.Graphics

class UpdatedGraphicsCookie(
  private val graphics: Graphics,
  color: Color = graphics.color,
  font: Font = graphics.font
) : Disposable {
  private val previousColor = graphics.color
  private val previousFont = graphics.font

  init {
    graphics.color = color
    graphics.font = font
  }

  override fun dispose() {
    graphics.color = previousColor
    graphics.font = previousFont
  }
}