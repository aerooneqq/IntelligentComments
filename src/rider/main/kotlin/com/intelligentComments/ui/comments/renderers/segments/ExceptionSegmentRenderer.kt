package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel

class ExceptionSegmentRenderer(
  model: ExceptionUiModel
) : LeftTextHeaderAndRightContentRenderer(model.name, model.content.contentSection.content), SegmentRenderer