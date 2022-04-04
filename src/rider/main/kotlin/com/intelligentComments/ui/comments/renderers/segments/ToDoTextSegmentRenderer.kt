package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.todo.ToDoTextContentSegmentUiModel

class ToDoTextSegmentRenderer(
  private val model: ToDoTextContentSegmentUiModel
) : LeftTextHeaderAndRightContentRenderer(
  model.header,
  model.content
)