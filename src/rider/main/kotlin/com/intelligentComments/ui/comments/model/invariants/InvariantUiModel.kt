package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.core.domain.core.Invariant
import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

abstract class InvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
) : UiInteractionModelBase(project, parent) {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, invariant: Invariant): InvariantUiModel {
      return when (invariant) {
        is TextInvariant -> TextInvariantUiModel(project, parent, invariant)
        else -> throw IllegalArgumentException(invariant.toString())
      }
    }
  }

  val borderColor
    get() = colorsProvider.getColorFor(borderColorKey)

  protected open val borderColorKey: ColorName = Colors.InvariantDefaultBorderColor
}