package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.invariants.AddNewInvariantRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantRenderer
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project

class AddNewInvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
) : UiInteractionModelBase(project, parent) {
  val borderColor
    get() = if (!mouseIn) {
      colorsProvider.getColorFor(Colors.AddNewInvariantBorderColor)
    } else {
      colorsProvider.getColorFor(Colors.AddNewInvariantBorderHoveredColor)
    }

  val text: String = "Add"
  val icon = AllIcons.General.Add

  override fun calculateStateHash(): Int {
    return 123;
  }

  override fun createRenderer(): InvariantRenderer = AddNewInvariantRenderer(this)
}