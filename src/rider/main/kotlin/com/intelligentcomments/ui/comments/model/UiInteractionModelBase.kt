package com.intelligentcomments.ui.comments.model

import com.intelligentcomments.ui.colors.ColorName
import com.intelligentcomments.ui.colors.Colors
import com.intelligentcomments.ui.colors.ColorsProvider
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.sections.SectionUiModel
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.components.service
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

  open fun dumpModel(): String = javaClass.simpleName
  abstract fun createRenderer(): Renderer
  abstract fun calculateStateHash(): Int

  open val backgroundColor: Color
    get() = if (myMouseIn) {
      colorsProvider.getColorFor(hoveredBackgroundColorKey)
    } else {
      colorsProvider.getColorFor(backgroundColorKey)
    }

  fun handleMouseIn(e: EditorMouseEvent): Boolean {
    myMouseIn = true
    return handleMouseInInternal(e)
  }

  protected open fun handleMouseInInternal(e: EditorMouseEvent): Boolean = false

  fun handleMouseOut(e: EditorMouseEvent): Boolean {
    myMouseIn = false
    return handleMouseOutInternal(e)
  }

  protected open fun handleMouseOutInternal(e: EditorMouseEvent): Boolean = false

  open fun handleLongMousePresence(e: EditorMouseEvent): Boolean {
    return false
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

interface ModelWithContent {
  val contentSection: SectionUiModel
}

interface ModelWithContentSegments {
  val content: ContentSegmentsUiModel
}