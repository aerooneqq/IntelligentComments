package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.content.code.CodeSegmentUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle

class CodeSegmentRenderer(
  codeSegment: CodeSegmentUiModel
) : TextRendererBase(codeSegment.code)