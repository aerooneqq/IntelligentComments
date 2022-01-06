package com.intelligentComments.ui.comments.model.highlighters

import com.intelligentComments.core.comments.HighlightersClickHandler
import com.intelligentComments.core.comments.RiderCommentsController
import com.intelligentComments.core.comments.listeners.getBounds
import com.intelligentComments.core.domain.core.TextHighlighter
import com.intelligentComments.core.domain.core.tryFindComment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.tryGetRootUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import java.awt.Point

class HighlighterUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val highlighter: TextHighlighter
) : UiInteractionModelBase(project, parent) {
  companion object {
    fun getFor(project: Project, parent: UiInteractionModelBase?, highlighter: TextHighlighter): HighlighterUiModel {
      return HighlighterUiModel(project, parent, highlighter)
    }
  }

  val startOffset = highlighter.startOffset
  val endOffset = highlighter.endOffset
  val weight = highlighter.attributes.weight
  val style = highlighter.attributes.style

  var textColor = highlighter.textColor
  var backgroundStyle = highlighter.backgroundStyle
  var underline = highlighter.attributes.underline

  override fun handleMouseIn(e: EditorMouseEvent): Boolean = applyMouseInOutAnimation(true)

  private fun applyMouseInOutAnimation(mouseIn: Boolean): Boolean {
    val result = highlighter.mouseInOutAnimation?.applyTo(this, mouseIn)
    if (result != null) {
      myMouseIn = mouseIn
      return result
    }

    return false
  }

  override fun handleMouseOut(e: EditorMouseEvent): Boolean = applyMouseInOutAnimation(false)

  override fun handleClick(e: EditorMouseEvent): Boolean {
    if (e.mouseEvent.isControlDown || (SystemInfo.isMac && e.mouseEvent.isMetaDown)) {
      return handleCtrlClick(e)
    }

    return handleUsualClick(e)
  }

  private fun handleUsualClick(e: EditorMouseEvent): Boolean {
    val root = tryGetRootUiModel(this)
    var point = e.mouseEvent.point
    val commentId = tryFindComment(highlighter)?.commentIdentifier

    if (commentId != null && root != null) {
      val rectangle = root.renderer.rectanglesModel?.getRectanglesFor(this)?.lastOrNull()
      val folding = project.getComponent(RiderCommentsController::class.java).getFolding(commentId, e.editor as EditorImpl)

      if (rectangle != null && folding != null) {
        val bounds = folding.getBounds()
        if (bounds != null) {
          point = Point(rectangle.x + bounds.x, rectangle.y + rectangle.height + bounds.y)
        }
      }
    }

    e.editor.project?.service<HighlightersClickHandler>()?.handleClick(highlighter, e.editor, point)
    return false
  }

  private fun handleCtrlClick(e: EditorMouseEvent): Boolean {
    e.editor.project?.service<HighlightersClickHandler>()?.handleCtrlClick(highlighter, e.editor)
    return false
  }

  override fun calculateStateHash(): Int {
    val bsHashCode = if (backgroundStyle == null) 1 else backgroundStyle.hashCode()
    return HashUtil.hashCode(highlighter.hashCode(), textColor.hashCode(), bsHashCode, underline.hashCode())
  }
}