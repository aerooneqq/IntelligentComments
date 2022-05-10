package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.`return`.ReturnUiModel

class ReturnSegmentRenderer(
  model: ReturnUiModel
) : LeftTextHeaderAndRightContentRenderer(model.headerText, model.content)