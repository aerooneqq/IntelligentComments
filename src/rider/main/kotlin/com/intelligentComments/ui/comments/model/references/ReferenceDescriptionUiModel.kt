package com.intelligentComments.ui.comments.model.references

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class ReferenceDescriptionUiModel(
  project: Project,
  parent: UiInteractionModelBase?
) : UiInteractionModelBase(project, parent) {
  override fun calculateStateHash(): Int {
    TODO("Not yet implemented")
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}