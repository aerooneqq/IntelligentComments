package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.Invariant
import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intellij.openapi.project.Project

open class InvariantUiModel(project: Project,
                            invariant: Invariant) : UiInteractionModelBase(project) {
    companion object {
        fun getFrom(project: Project, invariant: Invariant): InvariantUiModel {
            return when(invariant) {
                is TextInvariant -> TextInvariantUiModel(project, invariant)
                else -> throw IllegalArgumentException(invariant.toString())
            }
        }
    }
}

class TextInvariantUiModel(project: Project,
                           textInvariant: TextInvariant) : InvariantUiModel(project, textInvariant) {
    override val backgroundColorKey: ColorName = Colors.TextInvariantBackgroundColor
    override val hoveredBackgroundColorKey: ColorName = Colors.TextInvariantHoveredBackgroundColor

    val text = textInvariant.text
}