package com.intelligentComments.core.domain.core

import com.intelligentComments.core.utils.DocumentUtils
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement


class CommentIdentifier(val moniker: String, val rangeMarker: RangeMarker) : Comparable<CommentIdentifier> {
  companion object {
    fun create(document: Document, project: Project, rangeMarker: RangeMarker): CommentIdentifier {
      val moniker = DocumentUtils.tryGetMoniker(document, project) ?: throw IllegalArgumentException()
      return CommentIdentifier(moniker, rangeMarker)
    }
  }

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
}

interface CommentBase : UniqueEntity, Parentable {
  val rangeMarker: RangeMarker
  val commentIdentifier: CommentIdentifier

  fun isValid(): Boolean {
    return rangeMarker.isValid
  }

  fun recreate(editor: Editor): CommentBase
}