package com.intelligentcomments.ui.comments.model.content.references

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.GroupedReferencesSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.model.content.invariants.mergeSegmentsTexts
import com.intelligentcomments.ui.comments.renderers.segments.references.GroupedReferencesRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

class GroupedReferencesUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedReferencesSegment
) : GroupedContentUiModel(
  project,
  parent,
  model,
  listOf(
    object : ContentSegments {
      override val parent: Parentable = model
      override val segments: Collection<ContentSegment> = listOf(object : UniqueEntityImpl(), TextContentSegment {
        override val parent: Parentable = model
        override val highlightedText: HighlightedText = mergeSegmentsTexts(
          model.segments, this
        ) {
          extractTextFromReference(it)
        }
      })
    }
  ),
  getFirstLevelHeader(project, referenceSectionName, model)
) {
  override fun createRenderer(): Renderer {
    return GroupedReferencesRenderer(this)
  }
}

private const val referenceSectionName = "References"

private fun extractTextFromReference(segment: ContentSegment) = (segment as ReferenceContentSegment).name

