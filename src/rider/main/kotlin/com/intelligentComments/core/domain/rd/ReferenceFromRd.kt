package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rdclient.document.FrontendDocumentHost
import com.jetbrains.rdclient.document.getDocumentId
import com.jetbrains.rdclient.util.idea.toIdeaRange
import com.jetbrains.rdclient.util.idea.toRdTextRange

open class ReferenceFromRd(private val reference: RdReference) : UniqueEntityImpl(), Reference {
  companion object {
    fun getFrom(project: Project, reference: RdReference): ReferenceFromRd {
      return when (reference) {
        is RdXmlDocCodeEntityReference -> XmlDocCodeEntityReferenceFromRd(reference)
        is RdSandboxCodeEntityReference -> SandboxCodeEntityReferenceFromRd(project, reference)
        is RdHttpLinkReference -> HttpLinkReferenceFromRd(reference)
        else -> throw IllegalArgumentException(reference.toString())
      }
    }
  }

  override val rawValue: String = reference.rawValue
}

open class XmlDocCodeEntityReferenceFromRd(
  reference: RdXmlDocCodeEntityReference
) : ReferenceFromRd(reference), XmlDocCodeEntityReference {
}

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
}

fun Reference.toRdReference(project: Project): RdReference {
  return when(this) {
    is XmlDocCodeEntityReference -> RdXmlDocCodeEntityReference(rawValue)
    is SandboxCodeEntityReference -> RdSandboxCodeEntityReference(
      sandboxFileId,
      originalDocument?.getDocumentId(project),
      range.toRdTextRange(),
      rawValue
    )
    else -> throw IllegalArgumentException(this.javaClass.name)
  }
}