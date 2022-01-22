package com.intelligentComments.ui.util

import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.core.RectanglesModel
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

class SectionModelUtil {
  companion object {
    fun renderSection(
      g: Graphics,
      targetRegion: Rectangle,
      editorImpl: EditorImpl,
      rectanglesModel: RectanglesModel,
      model: SectionUiModel<ContentSegmentUiModel>
    ): Rectangle {
      val renderer = SegmentsRenderer.getRendererFor(model)
      return renderer.render(g, targetRegion, editorImpl, rectanglesModel, RenderAdditionalInfo.emptyInstance)
    }
  }
}