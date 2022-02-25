package com.intelligentComments.ui.comments.model.content.invariants

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.FrontendInvariantReferenceImpl
import com.intelligentComments.core.domain.impl.GroupedInvariantsSegments
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.renderers.segments.invariants.GroupedInvariantsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

private const val InvariantsSectionName = "Invariants"

class GroupedInvariantsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedInvariantsSegments
) : GroupedContentUiModel(
  project,
  parent,
  model,
  listOf(
    object : ContentSegments {
      override val parent: Parentable = model
      override val segments: Collection<ContentSegment> = listOf(object : UniqueEntityImpl(), TextContentSegment {
        override val parent: Parentable = model
        override val highlightedText: HighlightedText = mergeInvariantsTexts(project, model.segments, this)
      })

      override fun processSegments(strategy: ContentProcessingStrategy) {
      }
    }
  ),
  getFirstLevelHeader(project, InvariantsSectionName, model)
) {
  override fun createRenderer(): Renderer {
    return GroupedInvariantsRenderer(this)
  }
}

private fun mergeInvariantsTexts(
  project: Project,
  segments: List<ContentSegment>,
  parent: Parentable
): HighlightedText {
  var text: HighlightedText = HighlightedTextImpl.createEmpty(parent)
  for (index in segments.indices) {
    val invariant = segments[index] as TextInvariantSegment

    val nameText = invariant.name.text
    val nameParent = invariant.name.parent
    val invariantColor = project.service<ColorsProvider>().getColorFor(Colors.TextDefaultColor)
    val references = listOf(FrontendInvariantReferenceImpl(invariant))
    val highlighter = TextHighlighterImpl(null, 0, nameText.length, invariantColor, references)

    val nameWithHighlighter = HighlightedTextImpl(nameText, nameParent, highlighter)
    text = text.mergeWith(nameWithHighlighter)
    if (index < segments.size - 1) {
      text = text.mergeWith(", ")
    }
  }

  return text
}