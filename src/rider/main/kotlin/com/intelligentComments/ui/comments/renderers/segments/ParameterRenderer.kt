package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.params.AbstractParameterUiModel

class ParameterRenderer(
  model: AbstractParameterUiModel
) : LeftTextHeaderAndRightContentRenderer(model.name, model.description)