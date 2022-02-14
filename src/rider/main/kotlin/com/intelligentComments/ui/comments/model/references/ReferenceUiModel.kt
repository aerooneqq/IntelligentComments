package com.intelligentComments.ui.comments.model.references

import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class ReferenceUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  reference: Reference
) : UiInteractionModelBase(project, parent), ExpandableUiModel {
  companion object {
    fun getFrom(project: Project, reference: Reference): ReferenceUiModel {
      return when (reference) {
        else -> throw IllegalArgumentException(reference.toString())
      }
    }
  }

  val headerUiModel = HeaderUiModel(
    project,
    this,
    "",
    Colors.ReferenceHeaderBackgroundColor,
    Colors.ReferenceHeaderHoveredBackgroundColor
  )
  override var isExpanded: Boolean = true


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(headerUiModel.calculateStateHash(), isExpanded.hashCode())
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}