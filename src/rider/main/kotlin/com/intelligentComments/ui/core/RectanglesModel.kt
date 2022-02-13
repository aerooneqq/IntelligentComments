package com.intelligentComments.ui.core

import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.RectanglesModelBuildResult
import com.intelligentComments.ui.util.RectanglesModelUtil
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.util.application
import com.jetbrains.rd.util.getOrCreate
import java.awt.Point
import java.awt.Rectangle

class RectanglesModelHolder(private val uiModel: UiInteractionModelBase) {
  private var lastUpdateHash = 0
  private var previousBuildResult: RectanglesModelBuildResult? = null

  val model
    get() = previousBuildResult?.model


  fun revalidate(editor: EditorImpl, xDelta: Int, yDelta: Int): RectanglesModelBuildResult {
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

class RectanglesModel {
  private val rectanglesToElements = HashMap<Rectangle, MutableList<UiInteractionModelBase>>()
  private val elementsToRectangles = HashMap<UiInteractionModelBase, MutableList<Rectangle>>()

  private var sealed = false
  private var myWidth: Int = -1
  private var myHeight: Int = -1

  val width
    get() = myWidth
  val height
    get() = myHeight
  val allRectangles: Iterable<Rectangle>
    get() = rectanglesToElements.keys


  fun shiftAllRectanglesY(yDelta: Int) {
    application.assertIsDispatchThread()
    checkCanChange()

    for ((rect, _) in rectanglesToElements) {
      rect.y += yDelta
    }
  }

  fun getRectanglesFor(model: UiInteractionModelBase): Collection<Rectangle>? {
    return elementsToRectangles[model]
  }

  fun addElement(model: UiInteractionModelBase, rect: Rectangle) {
    application.assertIsDispatchThread()
    checkCanChange()

    val models = rectanglesToElements.getOrCreate(rect) { mutableListOf() }
    models.add(model)

    val rectangles = elementsToRectangles.getOrCreate(model) { mutableListOf() }
    rectangles.add(rect)
  }

  fun setSize(width: Int, height: Int) {
    checkCanChange()
    myWidth = width
    myHeight = height
  }

  private fun checkCanChange() {
    if (sealed) throw IllegalStateException("Model is already sealed, can't change it")
  }

  fun seal() {
    application.assertIsDispatchThread()
    if (sealed) return
    if (width == -1 || height == -1) throw IllegalStateException("Can't seal when width or height is not set")

    sealed = true
  }

  fun dispatchLongMousePresence(e: EditorMouseEvent, inlayBounds: Rectangle): Boolean {
    application.assertIsDispatchThread()

    val adjustedPoint = adjustPoint(e, inlayBounds)
    var anyUiChange = false

    executeWithRectangleAndModels { rect, model ->
      if (rect.contains(adjustedPoint)) {
        if (model.handleLongMousePresence(e)) {
          anyUiChange = true
        }
      }
    }

    return anyUiChange
  }

  private fun adjustPoint(e: EditorMouseEvent, inlayBounds: Rectangle): Point {
    return e.mouseEvent.point.apply {
      x -= inlayBounds.x
      y -= inlayBounds.y
    }
  }

  fun dispatchMouseMove(e: EditorMouseEvent, inlayBounds: Rectangle): Boolean {
    application.assertIsDispatchThread()

    val adjustedPoint = adjustPoint(e, inlayBounds)
    var anyUiChange = false

    executeWithRectangleAndModels { rect, model ->
      if (!rect.contains(adjustedPoint) && model.mouseIn) {
        if (model.handleMouseOut(e)) {
          anyUiChange = true
        }
      }
    }

    executeWithRectangleAndModels { rect, model ->
      if (rect.contains(adjustedPoint) && !model.mouseIn) {
        if (model.handleMouseIn(e)) {
          anyUiChange = true
        }
      }
    }

    return anyUiChange
  }

  private fun executeWithRectangleAndModels(action: (Rectangle, UiInteractionModelBase) -> Unit) {
    for ((rect, models) in rectanglesToElements) {
      for (model in models) {
        action(rect, model)
      }
    }
  }

  fun dispatchMouseClick(event: EditorMouseEvent, inlayBounds: Rectangle): Boolean {
    application.assertIsDispatchThread()
    var anyUiChange = false
    val point = event.mouseEvent.point.apply {
      x -= inlayBounds.x
      y -= inlayBounds.y
    }

    executeWithRectangleAndModels { rect, model ->
      if (rect.contains(point)) {
        if (model.handleClick(event)) {
          anyUiChange = true
        }
      }
    }

    return anyUiChange
  }
}