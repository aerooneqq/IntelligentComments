package com.intelligentComments.ui.comments.model.sections

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class SectionUiModel<T : UiInteractionModelBase>(
  project: Project,
  parent: UiInteractionModelBase?,
  val content: Collection<T>
) : UiInteractionModelBase(project, parent) {

  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(content) { it.calculateStateHash() }
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}