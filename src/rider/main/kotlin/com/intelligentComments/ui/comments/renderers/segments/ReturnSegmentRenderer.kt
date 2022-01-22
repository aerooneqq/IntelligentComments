package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel

class ReturnSegmentRenderer(
  model: ReturnUiModel
) : LeftTextHeaderAndRightContentRenderer(model.headerText, model.content)