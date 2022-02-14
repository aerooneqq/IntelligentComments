package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.core.domain.core.InvariantContentSegment
import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.renderers.invariants.InvariantRenderer
import com.intellij.openapi.project.Project

abstract class InvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  invariantContentSegment: InvariantContentSegment
) : ContentSegmentUiModel(project, parent, invariantContentSegment) {
  companion object {
    fun getFrom(
      project: Project,
      parent: UiInteractionModelBase?,
      segment: InvariantContentSegment
    ): InvariantUiModel {
      return when (val invariant = segment.invariant) {
        is TextInvariant -> TextInvariantUiModel(project, parent, segment, invariant)
        else -> throw IllegalArgumentException(invariant.toString())
      }
    }
  }

  val borderColor
    get() = colorsProvider.getColorFor(borderColorKey)

  protected open val borderColorKey: ColorName = Colors.InvariantDefaultBorderColor

  abstract override fun createRenderer(): InvariantRenderer
}