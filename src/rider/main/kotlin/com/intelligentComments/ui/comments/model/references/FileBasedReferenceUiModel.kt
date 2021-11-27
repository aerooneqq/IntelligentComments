package com.intelligentComments.ui.comments.model.references

import com.intelligentComments.core.domain.core.FileBasedReference
import com.intellij.openapi.project.Project

open class FileBasedReferenceUiModel(
  project: Project,
  reference: FileBasedReference
) : ReferenceUiModel(project, reference) {
  val filePath = reference.filePath

  override fun hashCode(): Int = super.hashCode() * filePath.hashCode()
  override fun equals(other: Any?): Boolean = other is FileBasedReferenceUiModel && other.hashCode() == hashCode()
}