package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.DependencyReference
import com.jetbrains.rd.ide.model.RdDependencyReference

class DependencyReferenceFromRd(private val reference: RdDependencyReference) : FileBasedReferenceFromRd(reference),
  DependencyReference {
  override val referenceName: String = reference.referenceName
  override val dependencyDescription: String = reference.dependencyDescription
}