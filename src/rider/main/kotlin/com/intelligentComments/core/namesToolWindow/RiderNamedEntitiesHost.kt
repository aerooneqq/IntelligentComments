package com.intelligentComments.core.namesToolWindow

import com.intellij.openapi.project.Project
import com.intellij.util.containers.enumMapOf
import com.jetbrains.rd.ide.model.RdFileNames
import com.jetbrains.rd.ide.model.RdNameKind
import com.jetbrains.rd.ide.model.RdSourceFileId
import com.jetbrains.rd.ide.model.rdCommentsModel
import com.jetbrains.rd.platform.util.idea.ProtocolSubscribedProjectComponent
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rider.projectView.solution
import java.util.EnumMap

class RiderNamedEntitiesHost(project: Project) : ProtocolSubscribedProjectComponent(project) {
  private val filesNamedEntities = hashMapOf<RdSourceFileId, HashMap<RdNameKind, RdFileNames>>()

  val fileEntitiesChanged = Property<RdFileNames?>(null)

  init {
    project.solution.rdCommentsModel.namedEntitiesChange.advise(project.lifetime) {
      val filesEntities = filesNamedEntities.getOrCreate(it.file.sourceFileId) { HashMap() }
      filesEntities[it.nameKind] = it
      fileEntitiesChanged.value = it
    }
  }


  fun getAllCurrentEntities(): Collection<RdFileNames> {
    return filesNamedEntities.flatMap { it.value.values }
  }
}