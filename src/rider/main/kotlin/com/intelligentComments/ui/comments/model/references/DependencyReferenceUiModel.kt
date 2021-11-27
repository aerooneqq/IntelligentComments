package com.intelligentComments.ui.comments.model.references

import com.intelligentComments.core.domain.core.DependencyReference
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class DependencyReferenceUiModel(
  project: Project,
  dependencyReference: DependencyReference
) : FileBasedReferenceUiModel(project, dependencyReference) {
  val referenceName = dependencyReference.referenceName
  val dependencyDescription = dependencyReference.dependencyDescription

  val descriptionUiModel = ReferenceDescriptionUiModel(project)

  override fun hashCode(): Int =
    (super.hashCode() * referenceName.hashCode() * dependencyDescription.hashCode()) % HashUtil.mod

  override fun equals(other: Any?): Boolean = other is DependencyReferenceUiModel && other.hashCode() == hashCode()
}