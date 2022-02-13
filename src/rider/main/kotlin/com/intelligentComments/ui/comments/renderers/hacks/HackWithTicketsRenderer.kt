package com.intelligentComments.ui.comments.renderers.hacks

import com.intelligentComments.core.domain.core.Hack
import com.intelligentComments.core.domain.core.HackWithTickets
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import java.awt.Graphics
import java.awt.Rectangle

interface HackRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getFrom(hack: Hack, project: Project): HackRenderer {
      return when (hack) {
        is HackWithTickets -> HackWithTicketsRenderer(hack, project)
        else -> throw IllegalArgumentException(hack.toString())
      }
    }
  }
}

class HackWithTicketsRenderer(hack: HackWithTickets, project: Project) : HackRenderer {
  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    TODO("Not yet implemented")
  }

  override fun calculateExpectedHeightInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    TODO("Not yet implemented")
  }

  override fun calculateExpectedWidthInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    TODO("Not yet implemented")
  }

  override fun accept(context: RectangleModelBuildContext) {
    TODO("Not yet implemented")
  }
}