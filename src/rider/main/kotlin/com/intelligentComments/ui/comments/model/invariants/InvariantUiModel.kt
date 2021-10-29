package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.core.domain.core.Invariant
import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
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