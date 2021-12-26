package com.intelligentComments.ui.comments.model.references

import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class ReferenceUiModel(
  project: Project,
  reference: Reference
) : UiInteractionModelBase(project), ExpandableUiModel {
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

  override fun hashCode(): Int = HashUtil.hashCode(headerUiModel.hashCode(), isExpanded.hashCode())
  override fun equals(other: Any?): Boolean = other is ReferenceUiModel && other.hashCode() == hashCode()
}