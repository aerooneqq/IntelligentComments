package com.intelligentComments.ui.comments.model

import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import java.awt.Color

abstract class UiInteractionModelBase(
  val project: Project,
  val parent: UiInteractionModelBase?
) {
  protected val colorsProvider = project.service<ColorsProvider>()

  protected var myMouseIn: Boolean = false
  val mouseIn
    get() = myMouseIn

  protected open val backgroundColorKey: ColorName = Colors.EmptyColor
  protected open val hoveredBackgroundColorKey: ColorName = Colors.EmptyColor

  abstract fun calculateStateHash(): Int

  open val backgroundColor: Color
    get() = if (myMouseIn) {
      colorsProvider.getColorFor(hoveredBackgroundColorKey)
    } else {
      colorsProvider.getColorFor(backgroundColorKey)
    }

  open fun handleMouseIn(e: EditorMouseEvent): Boolean {
    myMouseIn = true
    return true
  }

  open fun handleMouseOut(e: EditorMouseEvent): Boolean {
    myMouseIn = false
    return true
  }

  open fun handleClick(e: EditorMouseEvent): Boolean {
    return true
  }
}

fun tryGetRootUiModel(model: UiInteractionModelBase): RootUiModel? {
  var current: UiInteractionModelBase? = model
  while (current != null && current !is RootUiModel) {
    current = current.parent
  }

  return current as? RootUiModel
}

interface RootUiModel {
  val renderer: RendererWithRectangleModel
}

interface ExpandableUiModel {
  var isExpanded: Boolean
}