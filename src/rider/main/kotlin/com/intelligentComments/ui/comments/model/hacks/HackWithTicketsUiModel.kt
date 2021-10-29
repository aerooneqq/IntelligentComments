package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.HackWithTickets
import com.intellij.openapi.project.Project

class HackWithTicketsUiModel(hackWithTickets: HackWithTickets,
                             project: Project) : HackUiModel(hackWithTickets, project) {
}