package com.intelligentComments.ui.comments.model.content.references

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.GroupedReferencesSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.content.invariants.mergeSegmentsTexts
import com.intelligentComments.ui.comments.renderers.segments.references.GroupedReferencesRenderer
import com.intelligentComments.ui.core.Renderer
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

      override fun processSegments(strategy: ContentProcessingStrategy) {
      }
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

