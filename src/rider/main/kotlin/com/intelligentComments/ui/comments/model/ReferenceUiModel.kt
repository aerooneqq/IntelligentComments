package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.DependencyReference
import com.intelligentComments.core.domain.core.FileBasedReference
import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project

open class ReferenceUiModel(project: Project, reference: Reference) : UiInteractionModelBase(project), ExpandableUiModel {
    companion object {
        fun getFrom(project: Project, reference: Reference): ReferenceUiModel {
            return when(reference) {
                is DependencyReference -> DependencyReferenceUiModel(project, reference)
                else -> throw IllegalArgumentException(reference.toString())
            }
        }
    }

    val headerUiModel = HeaderUiModel(project, this, reference.referenceName, Colors.ReferenceHeaderBackgroundColor, Colors.ReferenceHeaderHoveredBackgroundColor)
    override var isExpanded: Boolean = true
}

class HeaderUiModel(project: Project,
                    val parent: ExpandableUiModel,
                    val text: String,
                    defaultBackground: ColorName,
                    hoveredBackground: ColorName) : UiInteractionModelBase(project) {
    override val backgroundColorKey: ColorName = defaultBackground
    override val hoveredBackgroundColorKey: ColorName = hoveredBackground

    override fun handleClick(e: EditorMouseEvent): Boolean {
        parent.isExpanded = !parent.isExpanded
        return super.handleClick(e)
    }
}

open class FileBasedReferenceUiModel(project: Project,
                                     reference: FileBasedReference): ReferenceUiModel(project, reference) {
    val filePath = reference.filePath
}

class DependencyReferenceUiModel(project: Project,
                                 dependencyReference: DependencyReference) : FileBasedReferenceUiModel(project, dependencyReference) {
    val referenceName = dependencyReference.referenceName
    val dependencyDescription = dependencyReference.dependencyDescription

    val descriptionUiModel = ReferenceDescriptionUiModel(project)
}

class ReferenceDescriptionUiModel(project: Project) : UiInteractionModelBase(project)