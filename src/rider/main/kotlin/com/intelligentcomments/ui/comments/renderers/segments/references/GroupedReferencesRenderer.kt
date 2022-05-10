package com.intelligentcomments.ui.comments.renderers.segments.references

import com.intelligentcomments.ui.comments.model.content.references.GroupedReferencesUiModel
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer

class GroupedReferencesRenderer(
  model: GroupedReferencesUiModel
) : LeftTextHeaderAndRightContentRenderer(model.header, model.content)