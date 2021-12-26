package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel

class GroupedSeeAlsoRenderer(
  model: GroupedSeeAlsoUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, listOf(model.description))