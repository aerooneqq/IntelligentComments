package com.intelligentComments.ui.comments.model.content.references

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.references.ReferencesRendererImpl
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ReferenceUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  reference: ReferenceContentSegment
) : ContentSegmentUiModel(project, parent) {
  val name = HighlightedTextUiWrapper(project, parent, reference.name)
  val content = ContentSegmentsUiModel(project, this, reference.description.content)

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(name.calculateStateHash(), content.calculateStateHash())
  }

  override fun createRenderer(): Renderer = ReferencesRendererImpl(this)
}