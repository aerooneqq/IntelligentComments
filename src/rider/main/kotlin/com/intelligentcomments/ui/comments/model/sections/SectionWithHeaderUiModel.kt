package com.intelligentcomments.ui.comments.model.sections

import com.intelligentcomments.ui.comments.model.ExpandableUiModel
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.renderers.segments.SegmentsRendererWithHeader
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project
import javax.swing.Icon

class SectionWithHeaderUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  content: Collection<ContentSegmentUiModel>,
  icon: Icon,
  headerText: HeaderTextInfo
) : SectionUiModel(project, parent, content), ExpandableUiModel {
  override var isExpanded: Boolean = true

  val headerUiModel = SectionHeaderUiModel(project, this, icon, headerText)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(isExpanded.hashCode(), super.hashCode())
  }

  override fun createRenderer(): Renderer {
    return SegmentsRendererWithHeader(this)
  }
}