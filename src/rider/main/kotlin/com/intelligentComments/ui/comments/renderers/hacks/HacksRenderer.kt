package com.intelligentComments.ui.comments.renderers.hacks

import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import java.awt.Graphics
import java.awt.Rectangle

interface HacksRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getFrom(section: SectionWithHeaderUiModel, project: Project): HacksRenderer {
      return HacksRendererImpl(section, project)
    }
  }
}

class HacksRendererImpl(
  private val section: SectionWithHeaderUiModel,
  private val project: Project
) : VerticalSectionWithHeaderRenderer(section), HacksRenderer {
  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    TODO("Not yet implemented")
  }

  override fun calculateContentWidth(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    TODO("Not yet implemented")
  }

  override fun calculateContentHeight(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    TODO("Not yet implemented")
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    TODO("Not yet implemented")
  }
}