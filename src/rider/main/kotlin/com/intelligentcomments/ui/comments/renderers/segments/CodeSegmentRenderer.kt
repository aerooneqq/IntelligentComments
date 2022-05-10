package com.intelligentcomments.ui.comments.renderers.segments

import com.intelligentcomments.ui.comments.model.content.code.CodeSegmentUiModel

class CodeSegmentRenderer(
  codeSegment: CodeSegmentUiModel
) : TextRendererBase(codeSegment.code)