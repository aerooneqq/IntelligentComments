package com.intelligentComments.ui.comments.model

import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project

class HeaderUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  val text: String,
  defaultBackground: ColorName,
  hoveredBackground: ColorName
) : UiInteractionModelBase(project, parent) {
  override val backgroundColorKey: ColorName = defaultBackground
  override val hoveredBackgroundColorKey: ColorName = hoveredBackground

  override fun handleClick(e: EditorMouseEvent): Boolean {
    parent as ExpandableUiModel
    parent.isExpanded = !parent.isExpanded
    return super.handleClick(e)
  }


  override fun calculateStateHash(): Int {
    return text.hashCode()
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}