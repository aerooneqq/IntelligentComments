package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.FileBasedReference
import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.jetbrains.rd.ide.model.RdDependencyReference
import com.jetbrains.rd.ide.model.RdFileBasedReference
import com.jetbrains.rd.ide.model.RdReference
import java.nio.file.Path
import java.nio.file.Paths

open class ReferenceFromRd(private val reference: RdReference) : UniqueEntityImpl(), Reference {
  companion object {
    fun getFrom(reference: RdReference): ReferenceFromRd {
      return when (reference) {
        is RdDependencyReference -> DependencyReferenceFromRd(reference)
        else -> throw IllegalArgumentException(reference.toString())
      }
    }
  }

  override val referenceName: String = reference.referenceName
}

open class FileBasedReferenceFromRd(private val reference: RdFileBasedReference) : ReferenceFromRd(reference),
  FileBasedReference {
  private val myCachedPath = Paths.get(reference.filePath)

  override val filePath: Path = myCachedPath
}