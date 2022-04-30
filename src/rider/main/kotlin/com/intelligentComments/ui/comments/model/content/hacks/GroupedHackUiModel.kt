package com.intelligentComments.ui.comments.model.content.hacks

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.GroupedHacksSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.model.content.invariants.createStartTextOfNamedEntity
import com.intelligentComments.ui.comments.renderers.segments.GroupedHacksRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project


private const val hackSectionsName = "Hacks"
class GroupedHackUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: GroupedHacksSegment
) : GroupedContentUiModel(
  project,
  parent,
  segment,
  segment.segments.map { generateContentSegmentsForNamedEntity(NameKind.Hack, it) },
  getFirstLevelHeader(
    project,
    hackSectionsName,
    segment
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedHacksRenderer(this)
  }
}

fun generateContentSegmentsForNamedEntity(
  kind: NameKind,
  segment: ContentSegment
) : ContentSegments {
  val segments = mutableListOf<ContentSegment>()

  if (segment is ContentSegmentWithOptionalName) {
    val name = segment.name
    if (name != null) {
      val nameText = createStartTextOfNamedEntity(kind, name, segment)
      segments.add(createTextSegmentFor(nameText, segment))
    }
  }

  if (segment is ContentSegmentWithInnerContent) {
    segments.addAll(segment.content.content.segments)
  }

  return createContentSegmentsFor(segments, segment)
}

fun generateContentSegmentsUiModelForNamedEntity(
  kind: NameKind,
  segment: ContentSegment,
  project: Project,
  parent: UiInteractionModelBase
) : ContentSegmentsUiModel {
  return ContentSegmentsUiModel(project, parent, generateContentSegmentsForNamedEntity(kind, segment))
}