package com.intelligentcomments.core.domain.core

import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.invariants.TextInvariantUiModel
import com.intellij.openapi.project.Project

interface InvariantSegment : ContentSegment

interface TextInvariantSegment : InvariantSegment {
  val name: HighlightedText?
  val description: HighlightedText

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return TextInvariantUiModel(project, parent, this)
  }
}