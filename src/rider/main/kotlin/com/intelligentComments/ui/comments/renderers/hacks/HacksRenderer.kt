package com.intelligentComments.ui.comments.renderers.hacks

import com.intelligentComments.ui.comments.model.hacks.HackUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.renderers.VerticalSectionWithHeaderRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import java.awt.Graphics
import java.awt.Rectangle

interface HacksRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getFrom(section: SectionWithHeaderUiModel<HackUiModel>, project: Project): HacksRenderer {
      return HacksRendererImpl(section, project)
    }
  }
}

class HacksRendererImpl(
  private val section: SectionWithHeaderUiModel<HackUiModel>,
  private val project: Project
) : VerticalSectionWithHeaderRenderer<HackUiModel>(section), HacksRenderer {
  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    TODO("Not yet implemented")
  }

  override fun calculateContentWidth(editorImpl: EditorImpl): Int {
    TODO("Not yet implemented")
  }

  override fun calculateContentHeight(editorImpl: EditorImpl): Int {
    TODO("Not yet implemented")
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    TODO("Not yet implemented")
  }
}