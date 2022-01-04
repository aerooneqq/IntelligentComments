package com.intelligentComments.ui.comments.model.content.image

import com.intelligentComments.core.domain.core.ImageContentSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ImageContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  imageSegment: ImageContentSegment
) : ContentSegmentUiModel(project, parent, imageSegment) {
  val description: HighlightedTextUiWrapper?
  val imageHolder = ImageHolder(imageSegment)

  init {
    val description = imageSegment.description
    this.description = if (description != null) {
      HighlightedTextUiWrapper(project, this, description)
    } else {
      null
    }
  }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(description?.calculateStateHash() ?: 1, imageHolder.hashCode())
  }
}