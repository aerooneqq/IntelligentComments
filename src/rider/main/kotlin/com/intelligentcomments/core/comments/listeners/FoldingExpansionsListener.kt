package com.intelligentcomments.core.comments.listeners

import com.intelligentcomments.core.comments.EditModeFolding
import com.intelligentcomments.core.comments.RiderCommentsController
import com.intelligentcomments.core.domain.core.CommentBase
import com.intelligentcomments.core.domain.core.CommentIdentifier
import com.intelligentcomments.ui.comments.renderers.RendererWithRectangleModel
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.ex.FoldingListener
import com.intellij.openapi.editor.impl.FoldingModelImpl
import com.intellij.util.application

class FoldingExpansionsListener : FoldingListener {
  override fun onFoldRegionStateChange(region: FoldRegion) {
    if (isIntelligentCommentFolding(region) || !region.isExpanded) return

    application.invokeLater {
      region.editor.project?.let { project ->
        val editor = region.editor
        val model = editor.foldingModel as FoldingModelImpl
        val regions = model.getRegionsOverlappingWith(region.startOffset, region.endOffset)
        val commentsToUpdate = mutableSetOf<CommentBase>()
        val controller = project.getComponent(RiderCommentsController::class.java)
        for (innerRegion in regions) {
          if (innerRegion == region) continue

          if (innerRegion.getUserData(EditModeFolding) != null) {
            val id = CommentIdentifier.create(project, innerRegion, editor)
            val comment = controller.tryGetComment(id, editor)
            if (comment != null) {
              commentsToUpdate.add(comment)
            }
          } else if (innerRegion is CustomFoldRegion) {
            val renderer = innerRegion.renderer
            if (renderer is RendererWithRectangleModel) {
              commentsToUpdate.add(renderer.baseModel.comment)
            }
          }
        }

        if (commentsToUpdate.size != 0) {
          controller.reRenderComments(commentsToUpdate, editor)
        }
      }
    }
  }
}