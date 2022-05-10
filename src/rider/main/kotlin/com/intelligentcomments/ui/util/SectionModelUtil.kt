package com.intelligentcomments.ui.util

import com.intelligentcomments.ui.comments.model.sections.SectionUiModel
import com.intelligentcomments.ui.core.RectanglesModel
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

class SectionModelUtil {
  companion object {
    fun renderSection(
      g: Graphics,
      targetRegion: Rectangle,
      editor: Editor,
      rectanglesModel: RectanglesModel,
      model: SectionUiModel
    ): Rectangle {
      val renderer = model.createRenderer()
      return renderer.render(g, targetRegion, editor, rectanglesModel, RenderAdditionalInfo.emptyInstance)
    }
  }
}