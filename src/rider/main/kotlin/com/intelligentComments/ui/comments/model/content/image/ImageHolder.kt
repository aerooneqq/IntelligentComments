package com.intelligentComments.ui.comments.model.content.image

import com.intelligentComments.core.domain.core.ImageContentSegment
import java.awt.Image

class ImageHolder(private val imageContentSegment: ImageContentSegment) {
  companion object {
    const val maxDimension = 500
  }

  var width = -1
    get() {
      initializeIfNeeded()
      return field
    }
    private set

  var height = -1
    get() {
      initializeIfNeeded()
      return field
    }
    private set

  val image: Image
    get() = imageContentSegment.image


  private var isInitialized = false

  private fun initializeIfNeeded() {
    if (isInitialized) return

    var imgWidth = image.getWidth(DummyImageObserver.instance)
    var imgHeight = image.getHeight(DummyImageObserver.instance)

    val maxImgDimension = Integer.max(imgWidth, imgHeight)
    val minImgDimension = Integer.min(imgWidth, imgHeight)
    val scaleCoeff = minImgDimension.toDouble() / maxImgDimension
    if (maxImgDimension > maxDimension) {
      if (imgWidth == maxImgDimension) {
        imgWidth = maxDimension
        imgHeight = (scaleCoeff * maxDimension).toInt()
      } else {
        imgHeight = maxDimension
        imgWidth = (scaleCoeff * maxDimension).toInt()
      }
    }

    width = imgWidth
    height = imgHeight
    isInitialized = true
  }
}