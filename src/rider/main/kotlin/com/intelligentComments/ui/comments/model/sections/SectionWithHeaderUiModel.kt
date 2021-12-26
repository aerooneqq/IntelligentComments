package com.intelligentComments.ui.comments.model.sections

import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import javax.swing.Icon

class SectionWithHeaderUiModel<T : UiInteractionModelBase>(
  project: Project,
  content: Collection<T>,
  icon: Icon,
  headerText: HeaderTextInfo
) : SectionUiModel<T>(project, content), ExpandableUiModel {
  override var isExpanded: Boolean = true

  val headerUiModel = SectionHeaderUiModel(project, icon, headerText, this)

  override fun hashCode(): Int = HashUtil.hashCode (isExpanded.hashCode(), super.hashCode())
  override fun equals(other: Any?): Boolean = other is SectionWithHeaderUiModel<*> && other.hashCode() == hashCode()
}