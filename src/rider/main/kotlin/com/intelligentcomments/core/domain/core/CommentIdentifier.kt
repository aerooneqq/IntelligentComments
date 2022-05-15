package com.intelligentcomments.core.domain.core

import com.intelligentcomments.core.comments.states.EditorId
import com.intelligentcomments.core.comments.states.getEditorId
import com.intelligentcomments.core.utils.DocumentUtils
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project

class CommentIdentifier(
  val moniker: String,
  val rangeMarker: RangeMarker,
  val editorId: EditorId
) : Comparable<CommentIdentifier> {
  companion object {
    fun create(project: Project, rangeMarker: RangeMarker, editor: Editor): CommentIdentifier {
      val moniker = DocumentUtils.tryGetMoniker(rangeMarker.document, project) ?: throw IllegalArgumentException()
      val editorId = editor.getEditorId() ?: throw IllegalArgumentException()

      return CommentIdentifier(moniker, rangeMarker, editorId)
    }
  }


  val isValid
    get() = rangeMarker.isValid

  override fun hashCode(): Int {
    return HashUtil.hashCode(moniker.hashCode(), rangeMarker.startOffset, rangeMarker.endOffset)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CommentIdentifier

    return hashCode() == other.hashCode()
  }

  override fun compareTo(other: CommentIdentifier): Int {
    if (moniker != other.moniker) {
      throw Exception()
    }

    return rangeMarker.startOffset - other.rangeMarker.startOffset
  }

  override fun toString(): String {
    return "${rangeMarker.startOffset} - ${rangeMarker.endOffset}"
  }
}