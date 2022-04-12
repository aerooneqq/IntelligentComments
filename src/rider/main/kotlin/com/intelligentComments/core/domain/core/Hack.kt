package com.intelligentComments.core.domain.core

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.hacks.HackWithTicketsUiModel
import com.intellij.openapi.project.Project

interface HackWithTicketsContentSegment : ContentSegment {
  val content: EntityWithContentSegments

  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return HackWithTicketsUiModel(project, parent, this)
  }
}