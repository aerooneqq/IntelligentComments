package com.intelligentComments.core.comments

import com.intelligentComments.core.comments.listeners.GutterMarkVisibilityMouseMoveListener
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.settings.CommentsDisplayKind
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.renderers.DocCommentSwitchRenderModeGutterMark
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.openapi.project.Project
import com.intellij.refactoring.suggested.range
import com.intellij.util.ui.update.MergingUpdateQueue
import com.intellij.util.ui.update.Update

class CommentsGutterMarksManager(project: Project) {
  private val commentsGuttersVisibility = HashMap<CommentBase, Boolean>()
  private val settings = RiderIntelligentCommentsSettingsProvider.getInstance()
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val statesManager = project.getComponent(RiderCommentsStateManager::class.java)
  private val visibleGutters = HashSet<DocCommentSwitchRenderModeGutterMark>()

  private val name = "${GutterMarkVisibilityMouseMoveListener::class.java.name}::queue"
  private val queue = MergingUpdateQueue(name, 300, true, null)


  fun queueUpdate(editor: Editor, offset: Int) {
    queue.queue(object : Update(this) {
      override fun run() {
        updateGutterFor(editor, offset)
      }
    })
  }

  fun getGutterVisibilityFor(comment: CommentBase) = commentsGuttersVisibility[comment] ?: false

  private fun updateGutterFor(editor: Editor, offset: Int) {
    if (settings.commentsDisplayKind.value == CommentsDisplayKind.Code) return

    val foldings = (editor.foldingModel as FoldingModelImpl).getRegionsOverlappingWith(offset, offset)
    val renderer = foldings.filterIsInstance<CustomFoldRegion>().firstOrNull()?.renderer as? RendererWithRectangleModel
    if (renderer == null) {
      val comment = controller.findNearestCommentToOffset(editor, offset)
      clearVisibleGutters()

      if (comment != null) {
        val offsetLine = editor.document.getLineNumber(offset)
        val commentStartLine = editor.document.getLineNumber(comment.identifier.rangeMarker.startOffset)
        val range = comment.identifier.rangeMarker.range
        val gutterMark = comment.correspondingHighlighter.gutterIconRenderer as DocCommentSwitchRenderModeGutterMark

        if (range != null && (range.contains(offset) || offsetLine == commentStartLine)) {
          val state = statesManager.getExistingCommentState(editor, comment.identifier)
          if (state != null && !state.isInRenderMode) {
            commentsGuttersVisibility[comment] = true
            gutterMark.isVisible = true
            visibleGutters.add(gutterMark)
          }
        }
      }

      updateEditorGutter(editor)
      return
    }

    commentsGuttersVisibility[renderer.baseModel.comment] = true
    val gutter = renderer.gutterMark
    if (gutter != null) {
      clearVisibleGutters()
      gutter.isVisible = true
      visibleGutters.add(gutter)
    }

    updateEditorGutter(editor)
  }

  private fun clearVisibleGutters() {
    visibleGutters.forEach { it.isVisible = false }
    visibleGutters.clear()
  }

  fun makeGutterVisibleImmediately(gutter: DocCommentSwitchRenderModeGutterMark, editor: Editor) {
    gutter.isVisible = true
    updateEditorGutter(editor)
  }

  private fun updateEditorGutter(editor: Editor) {
    (editor.gutter as EditorGutterComponentEx).apply {
      revalidate()
      repaint()
    }
  }
}