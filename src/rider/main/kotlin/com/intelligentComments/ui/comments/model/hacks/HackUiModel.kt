package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.Hack
import com.intelligentComments.core.domain.core.HackWithTickets
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intellij.openapi.project.Project

open class HackUiModel(private val hack: Hack, project: Project) : UiInteractionModelBase(project) {
    companion object {
        fun getFrom(project: Project, hack: Hack): HackUiModel {
            return when(hack) {
                is HackWithTickets -> HackWithTicketsUiModel(hack, project)
                else -> throw IllegalArgumentException(hack.toString())
            }
        }
    }
}