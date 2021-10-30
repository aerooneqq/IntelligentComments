package com.intelligentComments.ui.comments.model.hacks

import com.intelligentComments.core.domain.core.Hack
import com.intelligentComments.core.domain.core.HackWithTickets
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.HeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class HackUiModel(private val hack: Hack, project: Project) : UiInteractionModelBase(project), ExpandableUiModel {
    companion object {
        fun getFrom(project: Project, hack: Hack): HackUiModel {
            return when(hack) {
                is HackWithTickets -> HackWithTicketsUiModel(hack, project)
                else -> throw IllegalArgumentException(hack.toString())
            }
        }
    }

    val description = ContentSegmentsUiModel(project, hack.description)
    val blockingReferences = hack.blockingReferences.map { ReferenceUiModel(project, it) }
    val headerUiModel = HeaderUiModel(project, this, hack.name, Colors.HackHeaderBackgroundColor, Colors.HackHeaderHoveredBackgroundColor)
    override var isExpanded: Boolean = true

    override fun hashCode(): Int {
        val hash = (description.hashCode() * HashUtil.calculateHashFor(blockingReferences) * headerUiModel.hashCode())
        return hash % HashUtil.mod
    }

    override fun equals(other: Any?): Boolean = other is HackUiModel && other.hashCode() == hashCode()
}