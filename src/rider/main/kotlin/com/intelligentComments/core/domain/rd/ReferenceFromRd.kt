package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.psi.impl.source.resolve.reference.impl.providers.IdRefReference
import com.jetbrains.rd.ide.model.RdCodeEntityReference
import com.jetbrains.rd.ide.model.RdExternalReference
import com.jetbrains.rd.ide.model.RdHttpLinkReference
import com.jetbrains.rd.ide.model.RdReference

open class ReferenceFromRd(private val reference: RdReference) : UniqueEntityImpl(), Reference {
  companion object {
    fun getFrom(reference: RdReference): ReferenceFromRd {
      return when (reference) {
        is RdCodeEntityReference -> CodeEntityReferenceFromRd(reference)
        is RdHttpLinkReference -> HttpLinkReferenceFromRd(reference)
        else -> throw IllegalArgumentException(reference.toString())
      }
    }
  }
}

open class CodeEntityReferenceFromRd(reference: RdCodeEntityReference) : ReferenceFromRd(reference), CodeEntityReference {
  override val rawMemberName: String
    get() = TODO("Not yet implemented")
}

open class ExternalReferenceFromRd(reference: RdExternalReference) : ReferenceFromRd(reference), ExternalReference

open class HttpLinkReferenceFromRd(reference: RdHttpLinkReference) : ExternalReferenceFromRd(reference), HttpLinkReference {
  override val rawLink: String
    get() = TODO("Not yet implemented")
}