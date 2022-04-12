package com.intelligentComments.core.domain.core

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.invariants.TextInvariantUiModel
import com.intellij.openapi.project.Project

interface InvariantSegment : ContentSegment

interface TextInvariantSegment : InvariantSegment {
  val name: HighlightedText
  val description: EntityWithContentSegments

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return TextInvariantUiModel(project, parent, this)
  }
}