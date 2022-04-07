package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rdclient.document.FrontendDocumentHost
import com.jetbrains.rdclient.document.getFirstDocumentId
import com.jetbrains.rdclient.util.idea.toIdeaRange
import com.jetbrains.rdclient.util.idea.toRdTextRange
import java.io.File

open class ReferenceFromRd(reference: RdReference) : UniqueEntityImpl(), Reference {
  companion object {
    fun getFrom(project: Project, reference: RdReference): ReferenceFromRd {
      return when (reference) {
        is RdProxyReference -> ProxyReferenceFromRd(reference)
        is RdXmlDocCodeEntityReference -> XmlDocCodeEntityReferenceFromRd(reference)
        is RdSandboxCodeEntityReference -> SandboxCodeEntityReferenceFromRd(project, reference)
        is RdHttpLinkReference -> HttpLinkReferenceFromRd(reference)
        is RdFileReference -> FileReferenceFromRd(reference)
        is RdNamedEntityReference -> InvariantReferenceFromRd(reference)
        else -> throw IllegalArgumentException(reference.toString())
      }
    }
  }

  override val rawValue: String = reference.rawValue
}

open class XmlDocCodeEntityReferenceFromRd(
  reference: RdXmlDocCodeEntityReference
) : ReferenceFromRd(reference), XmlDocCodeEntityReference

open class SandboxCodeEntityReferenceFromRd(
  private val project: Project,
  private val reference: RdSandboxCodeEntityReference
) : ReferenceFromRd(reference), SandboxCodeEntityReference {
  override val originalDocument: Document?
    get() {
      return FrontendDocumentHost.getInstance(project).openedDocuments[reference.originalDocumentId]
    }

  override val sandboxFileId: String = reference.sandboxFileId
  override val range: TextRange = reference.range.toIdeaRange()
}

open class ExternalReferenceFromRd(reference: RdExternalReference) : ReferenceFromRd(reference), ExternalReference

open class HttpLinkReferenceFromRd(reference: RdHttpLinkReference) : ExternalReferenceFromRd(reference), HttpLinkReference {
  override val rawLink: String = reference.rawValue
  override val displayName: String = reference.displayName
}

class ProxyReferenceFromRd(reference: RdProxyReference): ReferenceFromRd(reference), ProxyReference {
  override val realReferenceId: Int = reference.realReferenceId
}

fun Reference.toRdReference(project: Project): RdReference {
  return when(this) {
    is ProxyReference -> RdProxyReference(realReferenceId, rawValue)
    is XmlDocCodeEntityReference -> RdXmlDocCodeEntityReference(rawValue)
    is SandboxCodeEntityReference -> RdSandboxCodeEntityReference(
      sandboxFileId,
      originalDocument?.getFirstDocumentId(project),
      range.toRdTextRange(),
      rawValue
    )
    is HttpLinkReference -> RdHttpLinkReference(displayName, rawValue)
    is NamedEntityReference -> RdNamedEntityReference(nameKind.toRdNameKind(), this.name, this.name)
    else -> throw IllegalArgumentException(this.javaClass.name)
  }
}

fun NameKind.toRdNameKind(): RdNameKind {
  return when(this) {
    NameKind.Hack -> RdNameKind.Hack
    NameKind.Invariant -> RdNameKind.Invariant
    NameKind.Todo -> RdNameKind.Todo
    else -> throw IllegalArgumentException(this.toString())
  }
}

fun RdNameKind.toNameKind(): NameKind {
  return when(this) {
    RdNameKind.Todo -> NameKind.Todo
    RdNameKind.Hack -> NameKind.Hack
    RdNameKind.Invariant -> NameKind.Invariant
    else -> throw IllegalArgumentException(this.toString())
  }
}

class ReferenceContentSegmentFromRd(
  contentSegment: RdReferenceContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(contentSegment, parent), ReferenceContentSegment {
  override val reference: Reference = ReferenceFromRd.getFrom(project, contentSegment.reference)
  override val description: EntityWithContentSegments = EntityWithContentSegmentsFromRd(contentSegment.description, this, project)
  override val name: HighlightedText = contentSegment.name.toIdeaHighlightedText(project, this)
}

class FileReferenceFromRd(reference: RdFileReference) : ExternalReferenceFromRd(reference), FileReference {
  override val file: File? = try { File(reference.path) } catch (e: Exception) { null }
}

class InvariantReferenceFromRd(reference: RdNamedEntityReference) : ReferenceFromRd(reference), NamedEntityReference {
  override val name: String = reference.name
  override val nameKind: NameKind = reference.nameKind.toNameKind()
}