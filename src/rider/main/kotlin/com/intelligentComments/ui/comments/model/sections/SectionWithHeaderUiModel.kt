package com.intelligentComments.ui.comments.model.sections

import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import javax.swing.Icon

class SectionWithHeaderUiModel<T : UiInteractionModelBase>(
  project: Project,
  parent: UiInteractionModelBase?,
  content: Collection<T>,
  icon: Icon,
  headerText: HeaderTextInfo
) : SectionUiModel<T>(project, parent, content), ExpandableUiModel {
  override var isExpanded: Boolean = true

  val headerUiModel = SectionHeaderUiModel(project, this, icon, headerText)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(isExpanded.hashCode(), super.hashCode())
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}