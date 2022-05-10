package com.intelligentcomments.ui.comments.model.content.invariants

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.GroupedInvariantsSegment
import com.intelligentcomments.core.domain.impl.HighlightedTextImpl
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.invariants.GroupedInvariantsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

private const val InvariantsSectionName = "Invariants"

class GroupedInvariantsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedInvariantsSegment
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
          extractTextFromInvariant(it)
        }
      })
    }
  ),
  getFirstLevelHeader(project, InvariantsSectionName, model)
) {
  override fun createRenderer(): Renderer {
    return GroupedInvariantsRenderer(this)
  }
}

private fun extractTextFromInvariant(segment: ContentSegment): HighlightedText {
  val name = (segment as TextInvariantSegment).name
  if (name != null) {
    return createStartTextOfNamedEntity(NameKind.Invariant, name, segment).mergeWith(segment.description)
  }

  return segment.description
}

fun createStartTextOfNamedEntity(kind: NameKind, name: HighlightedText, parentSegment: ContentSegment): HighlightedText {
  val kindText = "$kind ("
  val kindHighlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(parentSegment, kindText.length)
  val kindHighlightedText = HighlightedTextImpl(kindText, null, kindHighlighter)

  val endText = "): "
  val endTextHighlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(parentSegment, endText.length)
  val endHighlightedText = HighlightedTextImpl(endText, null, endTextHighlighter)

  return HighlightedTextImpl.createEmpty(parentSegment)
    .mergeWith(kindHighlightedText)
    .mergeWith(name)
    .mergeWith(endHighlightedText)
}

fun mergeSegmentsTexts(
  segments: List<ContentSegment>,
  parent: Parentable,
  textSelector: (ContentSegment) -> HighlightedText
): HighlightedText {
  var text: HighlightedText = HighlightedTextImpl.createEmpty(parent)
  for (index in segments.indices) {
    val name = textSelector(segments[index])

    text = text.mergeWith(name)
    if (index < segments.size - 1) {
      val delimiter = ", "
      val firstHighlighter = name.highlighters.firstOrNull()

      var highlighter: TextHighlighter? = null
      if (firstHighlighter != null) {
        highlighter = CommonsHighlightersFactory.createHighlighter(delimiter.length, firstHighlighter.textColor)
      }

      text = text.mergeWith(HighlightedTextImpl(delimiter, highlighter))
    }
  }

  return text
}