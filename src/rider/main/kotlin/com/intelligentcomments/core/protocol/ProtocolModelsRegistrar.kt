package com.intelligentcomments.core.protocol

import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdCommentsModel
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.projectView.solution

class ProtocolModelsRegistrar(project: Project) : LifetimedProjectComponent(project) {
  init {
    project.solution.protocol!!.serializers.registerSerializersOwnerOnce(RdCommentsModel)
  }
}