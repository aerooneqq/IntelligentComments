package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.core.domain.core.InvariantContentSegment
import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.invariants.InvariantRenderer
import com.intelligentComments.ui.comments.renderers.invariants.TextDefaultInvariantRenderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextInvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  invariantContentSegment: InvariantContentSegment,
  textInvariant: TextInvariant,
) : InvariantUiModel(project, parent, invariantContentSegment) {
  override val backgroundColorKey: ColorName = Colors.TextInvariantBackgroundColor
  override val hoveredBackgroundColorKey: ColorName = Colors.TextInvariantHoveredBackgroundColor

  val text = textInvariant.text


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(text.hashCode())
  }

  override fun createRenderer(): InvariantRenderer = TextDefaultInvariantRenderer(this)
}