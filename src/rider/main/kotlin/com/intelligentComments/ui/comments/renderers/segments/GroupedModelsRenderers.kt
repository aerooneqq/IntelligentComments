package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.exceptions.GroupedExceptionUiModel
import com.intelligentComments.ui.comments.model.content.hacks.GroupedHackUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedTypeParamsUiModel
import com.intelligentComments.ui.comments.model.content.remarks.GroupedRemarksUiModel
import com.intelligentComments.ui.comments.model.content.`return`.GroupedReturnUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.summary.GroupedSummaryUiModel
import com.intelligentComments.ui.comments.model.content.todo.GroupedToDoUiModel

class GroupedSeeAlsoRenderer(
  model: GroupedSeeAlsoUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, listOf(model.description))

class GroupedReturnsRenderer(
  model: GroupedReturnUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)

class GroupedParamsRenderer(
  model: GroupedParamsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)

class GroupedTypeParamsRenderer(
  model: GroupedTypeParamsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)

class GroupedExceptionsRenderer(
  model: GroupedExceptionUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)

class GroupedSummariesRenderer(
  model: GroupedSummaryUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content, false)

class GroupedRemarksRenderer(
  model: GroupedRemarksUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)

class GroupedTodosRenderer(
  model: GroupedToDoUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)

class GroupedHacksRenderer(
  model: GroupedHackUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)