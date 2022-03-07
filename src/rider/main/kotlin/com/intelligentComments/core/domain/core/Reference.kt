package com.intelligentComments.core.domain.core

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
  val rawLink: String
}

interface FileReference : ExternalReference {
  val file: File?
}

interface InvariantReference : BackendReference {
  val name: String
}

interface ReferenceContentSegment : ContentSegment {
  val reference: Reference
  val name: HighlightedText
  val description: EntityWithContentSegments
}