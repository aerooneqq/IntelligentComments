package com.intelligentComments.core.domain.core

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange

interface Reference : UniqueEntity {
  val rawValue: String
}

interface ProxyReference : Reference {
  val realReferenceId: Int
}

interface CodeEntityReference : Reference

interface XmlDocCodeEntityReference : CodeEntityReference

interface SandboxCodeEntityReference : CodeEntityReference {
  val originalDocument: Document?
  val sandboxFileId: String
  val range: TextRange
}

interface ExternalReference : Reference

interface HttpLinkReference : ExternalReference {
  val rawLink: String
}

interface ReferenceContentSegment : ContentSegment {
  val reference: Reference
}