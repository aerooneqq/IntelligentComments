package com.intelligentComments.core.comments

import com.intelligentComments.core.comments.listeners.GutterMarkVisibilityMouseMoveListener
import com.intelligentComments.core.comments.states.RiderCommentsStateManager
import com.intelligentComments.core.comments.states.canChangeFromCodeToRender
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentComments.ui.comments.renderers.DocCommentSwitchRenderModeGutterMark
import com.intelligentComments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorGutterComponentEx
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.openapi.project.Project
import com.intellij.refactoring.suggested.range
import com.intellij.util.application
import com.intellij.util.ui.update.MergingUpdateQueue
import com.intellij.util.ui.update.Update

class CommentsGutterMarksManager(project: Project) {
  private val controller = project.getComponent(RiderCommentsController::class.java)
  private val statesManager = project.getComponent(RiderCommentsStateManager::class.java)
  private val visibleCommentsGutters = HashSet<CommentBase>()

  private val name = "${GutterMarkVisibilityMouseMoveListener::class.java.name}::queue"
  private val queue = MergingUpdateQueue(name, 200, true, null)


  fun queueUpdate(editor: Editor, offset: Int) {
    queue.queue(object : Update(this) {
      override fun run() {
        updateGutterFor(editor, offset)
      }
    })
  }

  fun ensureAllGuttersAreInvisible(editor: Editor) {
    application.executeOnPooledThread {
      for (comment in controller.getAllCommentsFor(editor)) {
        val gutterMark = comment.correspondingHighlighter.gutterIconRenderer as? DocCommentSwitchRenderModeGutterMark
        if (gutterMark != null) {
          gutterMark.isVisible = false
        }
      }

      application.invokeLater {
        updateEditorGutter(editor)
      }
    }
  }

  fun getGutterVisibilityFor(comment: CommentBase) = visibleCommentsGutters.contains(comment)

  private fun updateGutterFor(editor: Editor, offset: Int) {
    val renderer = tryFindRendererFor(editor, offset)
    if (renderer == null) {
      val comment = controller.findNearestCommentToOffset(editor, offset)
      clearVisibleGutters(editor)

      if (comment != null) {
        if (!comment.isValid()) return

        val offsetLine = editor.document.getLineNumber(offset)
        val commentStartLine = editor.document.getLineNumber(comment.identifier.rangeMarker.startOffset)
        val range = comment.identifier.rangeMarker.range
        val gutterMark = comment.correspondingHighlighter.gutterIconRenderer as? DocCommentSwitchRenderModeGutterMark ?: return
        if (!canChangeFromCodeToRender(editor)) {
          gutterMark.isVisible = false
          updateEditorGutter(editor)
          return
        }

        if (range != null && (range.contains(offset) || offsetLine == commentStartLine)) {
          val state = statesManager.getExistingCommentState(editor, comment.identifier)
          if (state != null && !state.isInRenderMode) {
            makeGutterVisible(gutterMark)
          }
        }
      }

      updateEditorGutter(editor)
      return
    }

    val gutter = renderer.gutterMark
    if (gutter != null) {
      if (!canChangeFromCodeToRender(editor)) {
        gutter.isVisible = false
      } else {
        clearVisibleGutters(editor)
        makeGutterVisible(gutter)
      }
    }

    updateEditorGutter(editor)
  }

  private fun tryFindRendererFor(editor: Editor, offset: Int): RendererWithRectangleModel? {
    val foldings = (editor.foldingModel as FoldingModelImpl).getRegionsOverlappingWith(offset, offset)
    return foldings.filterIsInstance<CustomFoldRegion>().firstOrNull()?.renderer as? RendererWithRectangleModel
  }

  private fun tryFindGutterFor(comment: CommentBase, editor: Editor): DocCommentSwitchRenderModeGutterMark? {
    val rangeMarker = comment.identifier.rangeMarker
    val offset = (rangeMarker.startOffset + rangeMarker.endOffset) / 2
    val renderer = tryFindRendererFor(editor, offset)
    if (renderer != null) {
      return renderer.gutterMark
    }

    return comment.correspondingHighlighter.gutterIconRenderer as? DocCommentSwitchRenderModeGutterMark
  }

  private fun makeGutterVisible(gutter: DocCommentSwitchRenderModeGutterMark) {
    gutter.isVisible = true
    visibleCommentsGutters.add(gutter.comment)
  }

  private fun clearVisibleGutters(editor: Editor) {
    visibleCommentsGutters.forEach { tryFindGutterFor(it, editor)?.isVisible = false }
    visibleCommentsGutters.clear()
  }

  fun makeGutterVisibleImmediately(comment: CommentBase, editor: Editor) {
    if (!canChangeFromCodeToRender(editor)) return
    val gutterMark = tryFindGutterFor(comment, editor) ?: return
    makeGutterVisible(gutterMark)
    updateEditorGutter(editor)
  }

  private fun updateEditorGutter(editor: Editor) {
    (editor.gutter as EditorGutterComponentEx).apply {
      revalidate()
      repaint()
    }
  }
}