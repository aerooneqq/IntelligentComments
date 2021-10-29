package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

class AddNewInvariantUiModel(project: Project) : UiInteractionModelBase(project) {
    val text: String = "Add"
}