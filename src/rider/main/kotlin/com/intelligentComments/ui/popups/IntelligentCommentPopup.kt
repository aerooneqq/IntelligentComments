package com.intelligentComments.ui.popups

import com.intelligentComments.ui.comments.model.CommentUiModelBase
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.use
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.popup.AbstractPopup
import com.jetbrains.rd.platform.util.getLogger
import java.awt.*
import java.awt.event.ActionListener
import java.awt.geom.Rectangle2D
import java.util.*
import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.SwingConstants
import kotlin.math.min

class IntelligentCommentPopup(
  model: CommentUiModelBase,
  editor: Editor
) : AbstractPopup() {
  val popupSize: Dimension


  init {
    val project = model.project
    val popupInnerComponent = IntelligentCommentPopupComponent(model, editor)
    val popupWidth = popupInnerComponent.cachedSize.width
    val maxHeight = 400
    val popupHeight = min(maxHeight, popupInnerComponent.cachedSize.height)

    popupSize = Dimension(popupWidth, popupHeight)
    val component = object : JBScrollPane(popupInnerComponent) {
      override fun getPreferredSize(): Dimension = popupSize
    }

    //:trollface:
    init(project, component, component, true, true, true, null,
      false, null, null, true, Collections.emptySet(), false, null, null, null,
      false, null, false, false, false, null, 0f,
      null, true, true, emptyArray<Component>(), null, SwingConstants.LEFT, true, emptyList<Pair<ActionListener, KeyStroke>>(),
      null, null, false, true, true, null, true, null)
  }


  private class IntelligentCommentPopupComponent(
    private val model: CommentUiModelBase,
    private val editor: Editor
  ) : JComponent() {
    companion object {
      private val logger = getLogger<IntelligentCommentPopupComponent>()

      const val defaultWidth = 300
      const val defaultHeight = 300
    }


    val cachedSize: Dimension


    init {
      val renderer = model.renderer
      renderer.invalidateRectangleModel(editor)
      val rectanglesModel = renderer.rectanglesModel
      cachedSize = if (rectanglesModel == null) {
        logger.error("Rectangles model was null after revalidating for $model")
        Dimension(defaultWidth, defaultHeight)
      } else {
        Dimension(rectanglesModel.width, rectanglesModel.height)
      }
    }


    override fun getPreferredSize(): Dimension {
      return cachedSize
    }

    override fun paint(g: Graphics?) {
      super.paint(g)
      if (g !is Graphics2D) return

      val targetRect: Rectangle2D = Rectangle(0, 0, cachedSize.width, cachedSize.height)

      UpdatedGraphicsCookie(g, color = editor.contentComponent.background).use {
        g.fillRect(targetRect.x.toInt(), targetRect.y.toInt(), targetRect.width.toInt(), targetRect.height.toInt())
      }

      model.renderer.paint(editor, g, targetRect, TextAttributes())
    }
  }
}