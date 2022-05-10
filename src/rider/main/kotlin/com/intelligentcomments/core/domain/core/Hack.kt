package com.intelligentcomments.core.domain.core

import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.hacks.HackWithTicketsUiModel
import com.intellij.openapi.project.Project

interface HackWithTicketsContentSegment : ContentSegmentWithOptionalName, ContentSegmentWithInnerContent {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return HackWithTicketsUiModel(project, parent, this)
  }
}