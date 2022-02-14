package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class IntelligentCommentContentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  content: IntelligentCommentContent
) : UiInteractionModelBase(project, parent) {
  private val mySegments = mutableListOf<ContentSegmentUiModel>()

  val segments: Collection<ContentSegmentUiModel> = mySegments

  init {
    for (segment in content.content.segments) mySegments.add(ContentSegmentUiModel.getFrom(project, this, segment))
  }


  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(mySegments) { it.calculateStateHash() }
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}