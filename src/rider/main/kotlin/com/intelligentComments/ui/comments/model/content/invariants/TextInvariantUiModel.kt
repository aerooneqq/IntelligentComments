package com.intelligentComments.ui.comments.model.content.invariants

import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.invariants.InvariantRenderer
import com.intelligentComments.ui.comments.renderers.invariants.TextDefaultInvariantRenderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextInvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: TextInvariant,
) : ContentSegmentUiModel(project, parent, segment) {
  override val backgroundColorKey: ColorName = Colors.TextInvariantBackgroundColor
  override val hoveredBackgroundColorKey: ColorName = Colors.TextInvariantHoveredBackgroundColor

  val name = HighlightedTextUiWrapper(project, this, segment.name)
  val description = HighlightedTextUiWrapper(project, this, segment.description)

  val borderColor
    get() = colorsProvider.getColorFor(Colors.InvariantDefaultBorderColor)

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(name.hashCode(), description.hashCode())
  }

  override fun createRenderer(): InvariantRenderer = TextDefaultInvariantRenderer(this)
}