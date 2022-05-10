package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.params.AbstractParameterUiModel

class ParameterRenderer(
  model: AbstractParameterUiModel
) : LeftTextHeaderAndRightContentRenderer(model.name, model.description)