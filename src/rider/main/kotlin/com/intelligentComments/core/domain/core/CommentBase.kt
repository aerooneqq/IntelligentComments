package com.intelligentComments.core.domain.core

import com.intelligentComments.core.utils.DocumentUtils
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project


class CommentIdentifier(val moniker: String, val rangeMarker: RangeMarker) : Comparable<CommentIdentifier> {
  companion object {
    fun create(document: Document, project: Project, rangeMarker: RangeMarker): CommentIdentifier {
      val moniker = DocumentUtils.tryGetMoniker(document, project) ?: throw IllegalArgumentException()
      return CommentIdentifier(moniker, rangeMarker)
    }
  }

  override fun hashCode(): Int {
    return (moniker.hashCode() * rangeMarker.startOffset * rangeMarker.endOffset) % 1000007
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CommentIdentifier

    if (moniker != other.moniker || other.rangeMarker.startOffset != rangeMarker.startOffset || other.rangeMarker.endOffset != rangeMarker.endOffset) {
      return false
    }

    return true
  }

  override fun compareTo(other: CommentIdentifier): Int {
    return hashCode() - other.hashCode()
  }
}

interface CommentBase : UniqueEntity {
  val rangeMarker: RangeMarker
  val commentIdentifier: CommentIdentifier
}