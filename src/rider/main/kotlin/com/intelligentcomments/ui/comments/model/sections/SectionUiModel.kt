package com.intelligentcomments.ui.comments.model.sections

import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.renderers.segments.DefaultSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class SectionUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  val content: Collection<ContentSegmentUiModel>
) : UiInteractionModelBase(project, parent) {
  override fun dumpModel(): String = "${super.dumpModel()}::${content.joinToString("\n") { it.dumpModel() }}\n"

  override fun calculateStateHash(): Int = HashUtil.calculateHashFor(content) { it.calculateStateHash() }

  override fun createRenderer(): Renderer = DefaultSegmentsRenderer(this)
}