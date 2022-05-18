package com.intelligentcomments.ui.comments.model.content.image

import com.intelligentcomments.core.domain.core.ImageContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.ImageSegmentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ImageContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val imageSegment: ImageContentSegment
) : ContentSegmentUiModel(project, parent) {
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


  override fun dumpModel() = "${super.dumpModel()}::${description?.dumpModel()}::${imageSegment.sourceReference.rawValue}"
  override fun calculateStateHash() = HashUtil.hashCode(description?.calculateStateHash() ?: 1, imageHolder.hashCode())
  override fun createRenderer() = ImageSegmentRenderer(this)
}