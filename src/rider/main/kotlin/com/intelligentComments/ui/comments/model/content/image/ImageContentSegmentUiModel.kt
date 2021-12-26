package com.intelligentComments.ui.comments.model.content.image

import com.intelligentComments.core.domain.core.ImageContentSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ImageContentSegmentUiModel(
  project: Project,
  imageSegment: ImageContentSegment
) : ContentSegmentUiModel(project, imageSegment) {
  val description: HighlightedTextUiWrapper?
  val imageHolder = ImageHolder(imageSegment)

  init {
    val description = imageSegment.description
    this.description = if (description != null) {
      HighlightedTextUiWrapper(project, description)
    } else {
      null
    }
  }

  override fun hashCode(): Int = HashUtil.hashCode(description.hashCode(), imageHolder.hashCode())
  override fun equals(other: Any?): Boolean = other is ImageContentSegmentUiModel && other.hashCode() == hashCode()
}