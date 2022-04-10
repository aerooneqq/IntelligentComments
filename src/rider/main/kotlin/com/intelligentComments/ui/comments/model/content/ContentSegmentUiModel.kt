package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.*
import com.intelligentComments.core.domain.rd.HackInlinedContentSegmentFromRd
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.code.CodeSegmentUiModel
import com.intelligentComments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.GroupedExceptionUiModel
import com.intelligentComments.ui.comments.model.content.hacks.GroupedHackUiModel
import com.intelligentComments.ui.comments.model.content.hacks.HackTextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.hacks.HackWithTicketsUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.invariants.GroupedInvariantsUiModel
import com.intelligentComments.ui.comments.model.content.invariants.TextInvariantUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedTypeParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.ParameterUiModel
import com.intelligentComments.ui.comments.model.content.params.TypeParamUiModel
import com.intelligentComments.ui.comments.model.content.references.GroupedReferencesUiModel
import com.intelligentComments.ui.comments.model.content.remarks.GroupedRemarksUiModel
import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.model.content.`return`.GroupedReturnUiModel
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.summary.GroupedSummaryUiModel
import com.intelligentComments.ui.comments.model.content.summary.SummaryUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.tickets.GroupedTicketsUiModel
import com.intelligentComments.ui.comments.model.content.tickets.TicketUiModel
import com.intelligentComments.ui.comments.model.content.todo.GroupedToDoUiModel
import com.intelligentComments.ui.comments.model.content.todo.ToDoTextContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.value.ValueUiModel
import com.intelligentComments.ui.comments.model.content.todo.ToDoWithTicketsUiModel
import com.intelligentComments.ui.comments.renderers.segments.GroupedHacksRenderer
import com.intellij.openapi.project.Project

abstract class ContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
) : UiInteractionModelBase(project, parent) {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, segment: ContentSegment): ContentSegmentUiModel {
      //ToDo: sth is definitely wrong here
      return when (segment) {
        is TextContentSegment -> TextContentSegmentUiModel(project, parent, segment)
        is ListContentSegment -> ListContentSegmentUiModel(project, parent, segment)
        is ImageContentSegment -> ImageContentSegmentUiModel(project, parent, segment)
        is TableContentSegment -> TableContentSegmentUiModel(project, parent, segment)
        is ParagraphContentSegment -> ParagraphUiModel(project, parent, segment)
        is TypeParamSegment -> TypeParamUiModel(project, parent, segment)
        is ParameterSegment -> ParameterUiModel(project, parent, segment)
        is ReturnSegment -> ReturnUiModel(project, parent, segment)
        is RemarksSegment -> RemarksUiModel(project, parent, segment)
        is ExceptionSegment -> ExceptionUiModel(project, parent, segment)
        is SeeAlsoSegment -> SeeAlsoUiModel.getFor(project, parent, segment)
        is GroupedSeeAlsoSegment -> GroupedSeeAlsoUiModel(project, parent, segment)
        is GroupedReturnSegment -> GroupedReturnUiModel(project, parent, segment)
        is GroupedParamSegment -> GroupedParamsUiModel(project, parent, segment)
        is GroupedTypeParamSegment -> GroupedTypeParamsUiModel(project, parent, segment)
        is GroupedExceptionsSegment -> GroupedExceptionUiModel(project, parent, segment)
        is GroupedSummarySegment -> GroupedSummaryUiModel(project, parent, segment)
        is GroupedRemarksSegment -> GroupedRemarksUiModel(project, parent, segment)
        is GroupedInvariantsSegment -> GroupedInvariantsUiModel(project, parent, segment)
        is GroupedReferencesSegment -> GroupedReferencesUiModel(project, parent, segment)
        is GroupedTodosSegment -> GroupedToDoUiModel(project, parent, segment)
        is GroupedTicketsSegment -> GroupedTicketsUiModel(project, parent, segment)
        is GroupedHacksSegment -> GroupedHackUiModel(project, parent, segment)
        is ExampleContentSegment -> ExampleSegmentUiModel(project, parent, segment)
        is SummaryContentSegment -> SummaryUiModel(project, parent, segment)
        is CodeSegment -> CodeSegmentUiModel(project, parent, segment)
        is ValueSegment -> ValueUiModel(project, parent, segment)
        is ToDoTextContentSegment -> ToDoTextContentSegmentUiModel(project, parent, segment)
        is HackTextContentSegment -> HackTextContentSegmentUiModel(project, parent, segment)
        is TextInvariantSegment -> TextInvariantUiModel(project, parent, segment)
        is ToDoWithTicketsContentSegment -> ToDoWithTicketsUiModel(project, parent, segment)
        is TicketContentSegment -> TicketUiModel(project, parent, segment)
        is HackWithTicketsContentSegment -> HackWithTicketsUiModel(project, parent, segment)
        is EntityWithContentSegments -> ContentSegmentsUiModel(project, parent, segment.content)
        else -> throw IllegalArgumentException(segment.javaClass.name)
      }
    }
  }
}