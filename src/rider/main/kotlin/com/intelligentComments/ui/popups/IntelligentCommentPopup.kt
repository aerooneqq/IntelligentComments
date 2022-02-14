package com.intelligentComments.ui.popups

import com.intelligentComments.ui.comments.model.CommentUiModelBase
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.use
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.popup.AbstractPopup
import com.jetbrains.rd.platform.util.getLogger
import java.awt.*
import java.awt.event.ActionListener
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
    val maxWidth = 600
    val popupWidth = min(maxWidth, popupInnerComponent.cachedSize.width)
    val maxHeight = 400
    val popupHeight = min(maxHeight, popupInnerComponent.cachedSize.height)

    popupSize = Dimension(popupWidth, popupHeight)
    val vScrollBehaviour = if (popupHeight < maxHeight) JBScrollPane.VERTICAL_SCROLLBAR_NEVER else JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    val hScrollbarBehaviour = if (popupWidth < maxWidth) JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER else JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED

    val component = object : JBScrollPane(popupInnerComponent, vScrollBehaviour, hScrollbarBehaviour) {
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
      const val padding = 10
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

      cachedSize.apply {
        width += 2 * padding
        height += 2 * padding
      }
    }


    override fun getPreferredSize(): Dimension {
      return cachedSize
    }

    override fun paint(g: Graphics?) {
      super.paint(g)
      if (g !is Graphics2D) return

      val targetRect = Rectangle(0, 0, cachedSize.width, cachedSize.height)

      UpdatedGraphicsCookie(g, color = editor.contentComponent.background).use {
        g.fillRect(targetRect.x, targetRect.y, targetRect.width, targetRect.height)
      }

      targetRect.apply {
        x += padding
        y += padding
      }

      model.renderer.paint(editor, g, targetRect, TextAttributes())
    }
  }
}