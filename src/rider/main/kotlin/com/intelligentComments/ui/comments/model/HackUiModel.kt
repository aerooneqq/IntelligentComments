package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.Hack
import com.intelligentComments.core.domain.core.HackWithTickets
import com.intellij.openapi.project.Project

open class HackUiModel(hack: Hack, project: Project) : UiInteractionModelBase(project) {
    companion object {
        fun getFrom(project: Project, hack: Hack): HackUiModel {
            return when(hack) {
                is HackWithTickets -> HackWithTicketsUiModel(hack, project)
                else -> throw IllegalArgumentException(hack.toString())
            }
        }
    }
}

class HackWithTicketsUiModel(hackWithTickets: HackWithTickets,
                             project: Project) : HackUiModel(hackWithTickets, project) {
}