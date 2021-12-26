package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.exceptions.GroupedExceptionUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedTypeParamsUiModel
import com.intelligentComments.ui.comments.model.content.`return`.GroupedReturnUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel

class GroupedSeeAlsoRenderer(
  model: GroupedSeeAlsoUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, listOf(model.description))

class GroupedReturnsRenderer(
  model: GroupedReturnUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content.content)

class GroupedParamsRenderer(
  model: GroupedParamsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content.content)

class GroupedTypeParamsRenderer(
  model: GroupedTypeParamsUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content.content)

class GroupedExceptionsRenderer(
  model: GroupedExceptionUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content.content)