package com.intelligentComments.core.domain.core

import java.nio.file.Path

interface Reference : UniqueEntity {
}

interface FileBasedReference : Reference {
    val filePath: Path
}

interface DependencyReference : FileBasedReference {
    val referenceName: String
    val dependencyDescription: String
}