package com.intelligentcomments.core.domain.impl

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.exceptions.GroupedExceptionUiModel
import com.intelligentcomments.ui.comments.model.content.hacks.GroupedHackUiModel
import com.intelligentcomments.ui.comments.model.content.invariants.GroupedInvariantsUiModel
import com.intelligentcomments.ui.comments.model.content.params.GroupedParamsUiModel
import com.intelligentcomments.ui.comments.model.content.params.GroupedTypeParamsUiModel
import com.intelligentcomments.ui.comments.model.content.references.GroupedReferencesUiModel
import com.intelligentcomments.ui.comments.model.content.remarks.GroupedRemarksUiModel
import com.intelligentcomments.ui.comments.model.content.`return`.GroupedReturnUiModel
import com.intelligentcomments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel
import com.intelligentcomments.ui.comments.model.content.summary.GroupedSummaryUiModel
import com.intelligentcomments.ui.comments.model.content.tickets.GroupedTicketsUiModel
import com.intelligentcomments.ui.comments.model.content.todo.GroupedToDoUiModel
import com.intellij.openapi.project.Project


open class GroupedSegmentsBase(override val parent: Parentable?) : UniqueEntityImpl(), Parentable

class GroupedSeeAlsoSegment(
  override val segments: List<SeeAlsoSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<SeeAlsoSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedSeeAlsoUiModel(project, parent, this)
  }
}

class GroupedReturnSegment(
  override val segments: List<ReturnSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ReturnSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedReturnUiModel(project, parent, this)
  }
}

class GroupedParamSegment(
  override val segments: List<ParameterSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ParameterSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedParamsUiModel(project, parent, this)
  }
}

class GroupedTypeParamSegment(
  override val segments: List<TypeParamSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<TypeParamSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedTypeParamsUiModel(project, parent, this)
  }
}

class GroupedExceptionsSegment(
  override val segments: List<ExceptionSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ExceptionSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedExceptionUiModel(project, parent, this)
  }
}

class GroupedSummarySegment(
  override val segments: List<SummaryContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<SummaryContentSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedSummaryUiModel(project, parent, this)
  }
}

class GroupedRemarksSegment(
  override val segments: List<RemarksSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<RemarksSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedRemarksUiModel(project, parent, this)
  }
}

class GroupedInvariantsSegment(
  override val segments: List<InvariantSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<InvariantSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedInvariantsUiModel(project, parent, this)
  }
}

class GroupedReferencesSegment(
  override val segments: List<ReferenceContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ReferenceContentSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedReferencesUiModel(project, parent, this)
  }
}

class GroupedTodosSegment(
  override val segments: List<ToDoWithTicketsContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<ToDoWithTicketsContentSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedToDoUiModel(project, parent, this)
  }
}

class GroupedTicketsSegment(
  override val segments: List<TicketContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<TicketContentSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedTicketsUiModel(project, parent, this)
  }
}

class GroupedHacksSegment(
  override val segments: List<HackWithTicketsContentSegment>,
  parent: Parentable?
) : GroupedSegmentsBase(parent), GroupedContentSegment<HackWithTicketsContentSegment> {
  override fun createUiModel(project: Project, parent: UiInteractionModelBase?): ContentSegmentUiModel {
    return GroupedHackUiModel(project, parent, this)
  }
}