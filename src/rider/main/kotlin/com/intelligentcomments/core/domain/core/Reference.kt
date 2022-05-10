package com.intelligentcomments.core.domain.core

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import java.io.File

interface Reference : UniqueEntity {
  val rawValue: String
}

interface BackendReference : Reference

interface ProxyReference : BackendReference {
  val realReferenceId: Int
}

interface CodeEntityReference : BackendReference

interface XmlDocCodeEntityReference : CodeEntityReference

interface SandboxCodeEntityReference : CodeEntityReference {
  val originalDocument: Document?
  val sandboxFileId: String
  val range: TextRange
}

interface ExternalReference : BackendReference

interface HttpLinkReference : ExternalReference {
  val displayName: String
  val rawLink: String
}

interface FileReference : ExternalReference {
  val file: File?
}

enum class NameKind {
  Invariant,
  Hack,
  Todo
}

fun NameKind.toPresentation(): String {
  return when (this) {
    NameKind.Todo -> "Todo"
    NameKind.Invariant -> "Invariant"
    NameKind.Hack -> "Hack"
    else -> throw IllegalArgumentException(this.toString())
  }
}

interface NamedEntityReference : BackendReference {
  val nameKind: NameKind
  val name: String
}

interface ReferenceContentSegment : ContentSegment {
  val reference: Reference
  val name: HighlightedText
  val description: EntityWithContentSegments
}

interface FrontendReference : Reference

interface FrontendReferenceWithContentSegment : FrontendReference {
  val model: ContentSegment
}

interface FrontendPopupSourceReference : FrontendReferenceWithContentSegment

interface FrontendTicketReference : FrontendPopupSourceReference