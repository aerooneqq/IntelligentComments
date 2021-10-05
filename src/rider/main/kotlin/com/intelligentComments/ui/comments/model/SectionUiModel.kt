package com.intelligentComments.ui.comments.model

import com.intellij.openapi.project.Project
import javax.swing.Icon

open class SectionUiModel<T : UiInteractionModelBase>(project: Project,
                                                      val content: Collection<T>) : UiInteractionModelBase(project)

class SectionWithHeaderUiModel<T : UiInteractionModelBase>(project: Project,
                                                           content: Collection<T>,
                                                           icon: Icon,
                                                           headerText: HeaderTextInfo) : SectionUiModel<T>(project, content), ExpandableUiModel {
    override var isExpanded: Boolean = true

    val headerUiModel = SectionHeaderUiModel(project, icon, headerText, this)
}

data class HeaderTextInfo(val expandedName: String, val closedName: String)
class SectionHeaderUiModel(project: Project,
                           val icon: Icon,
                           private val headerTextInfo: HeaderTextInfo,
                           private val parent: ExpandableUiModel) : UiInteractionModelBase(project) {
    val headerText: String
        get() = if (parent.isExpanded) headerTextInfo.expandedName else headerTextInfo.closedName

    override fun handleClick(): Boolean {
        parent.isExpanded = !parent.isExpanded
        return true
    }
}