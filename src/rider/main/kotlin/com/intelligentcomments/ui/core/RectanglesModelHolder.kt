package com.intelligentcomments.ui.core

import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.util.RectanglesModelBuildResult
import com.intelligentcomments.ui.util.RectanglesModelUtil
import com.intellij.openapi.editor.Editor
import com.intellij.util.application

class RectanglesModelHolder(private val uiModel: UiInteractionModelBase) {
  private var lastUpdateHash = 0
  private var previousBuildResult: RectanglesModelBuildResult? = null

  val model
    get() = previousBuildResult?.model


  fun revalidate(editor: Editor, xDelta: Int, yDelta: Int): RectanglesModelBuildResult {
    application.assertIsDispatchThread()
    val oldBuildResult = previousBuildResult
    val hashCode = uiModel.calculateStateHash()
    if (oldBuildResult != null && hashCode == lastUpdateHash) return oldBuildResult

    val buildResult = RectanglesModelUtil.buildRectanglesModel(editor, uiModel, xDelta, yDelta)
    lastUpdateHash = hashCode
    previousBuildResult = buildResult
    return buildResult
  }
}