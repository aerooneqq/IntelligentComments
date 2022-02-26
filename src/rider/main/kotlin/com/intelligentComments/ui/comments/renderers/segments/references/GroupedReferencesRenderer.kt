package com.intelligentComments.ui.comments.renderers.segments.references

import com.intelligentComments.ui.comments.model.content.references.GroupedReferencesUiModel
import com.intelligentComments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class GroupedReferencesRenderer(
  model: GroupedReferencesUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)