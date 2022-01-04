package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project

class AddNewInvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
) : InvariantUiModel(project, parent) {
  override val borderColorKey: ColorName
    get() = if (!mouseIn) Colors.AddNewInvariantBorderColor else Colors.AddNewInvariantBorderHoveredColor

  val text: String = "Add"
  val icon = AllIcons.General.Add

  override fun calculateStateHash(): Int {
    return 123;
  }
}