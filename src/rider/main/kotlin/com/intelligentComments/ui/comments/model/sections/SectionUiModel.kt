package com.intelligentComments.ui.comments.model.sections

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class SectionUiModel<T : UiInteractionModelBase>(
  project: Project,
  val content: Collection<T>
) : UiInteractionModelBase(project) {
  override fun hashCode(): Int = HashUtil.calculateHashFor(content)
  override fun equals(other: Any?): Boolean = other is SectionUiModel<*> && other.hashCode() == hashCode()
}