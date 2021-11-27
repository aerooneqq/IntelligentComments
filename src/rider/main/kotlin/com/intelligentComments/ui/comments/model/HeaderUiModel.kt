package com.intelligentComments.ui.comments.model

import com.intelligentComments.ui.colors.ColorName
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project

class HeaderUiModel(
  project: Project,
  val parent: ExpandableUiModel,
  val text: String,
  defaultBackground: ColorName,
  hoveredBackground: ColorName
) : UiInteractionModelBase(project) {
  override val backgroundColorKey: ColorName = defaultBackground
  override val hoveredBackgroundColorKey: ColorName = hoveredBackground

  override fun handleClick(e: EditorMouseEvent): Boolean {
    parent.isExpanded = !parent.isExpanded
    return super.handleClick(e)
  }

  override fun hashCode(): Int = text.hashCode()
  override fun equals(other: Any?): Boolean = other is HeaderUiModel && other.hashCode() == hashCode()
}