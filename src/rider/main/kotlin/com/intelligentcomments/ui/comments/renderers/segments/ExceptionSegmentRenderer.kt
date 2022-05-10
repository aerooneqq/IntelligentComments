package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.exceptions.ExceptionUiModel

class ExceptionSegmentRenderer(
  model: ExceptionUiModel
) : LeftTextHeaderAndRightContentRenderer(model.name, model.content.contentSection.content), SegmentRenderer