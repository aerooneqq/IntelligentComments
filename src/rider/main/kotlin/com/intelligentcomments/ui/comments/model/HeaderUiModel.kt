package com.intelligentcomments.ui.comments.model

import com.intelligentcomments.ui.colors.ColorName
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
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


  override fun dumpModel(): String = "${super.dumpModel()}::$text::${backgroundColorKey}::${hoveredBackgroundColorKey}"

  override fun calculateStateHash(): Int {
    return text.hashCode()
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}