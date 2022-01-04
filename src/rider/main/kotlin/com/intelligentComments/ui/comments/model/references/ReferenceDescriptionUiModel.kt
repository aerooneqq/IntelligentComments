package com.intelligentComments.ui.comments.model.references

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

class ReferenceDescriptionUiModel(
  project: Project,
  parent: UiInteractionModelBase?
) : UiInteractionModelBase(project, parent) {
  override fun calculateStateHash(): Int {
    TODO("Not yet implemented")
  }
}