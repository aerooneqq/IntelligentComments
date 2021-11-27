package com.intelligentComments.core.domain.core

import java.nio.file.Path

interface Reference : UniqueEntity {
  val referenceName: String
}

interface FileBasedReference : Reference {
  val filePath: Path
}

interface DependencyReference : FileBasedReference {
  val dependencyDescription: String
}