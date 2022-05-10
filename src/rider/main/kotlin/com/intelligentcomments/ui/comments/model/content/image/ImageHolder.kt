package com.intelligentcomments.ui.comments.model.content.image

import com.intelligentcomments.core.domain.core.FileReference
import com.intelligentcomments.core.domain.core.ImageContentSegment
import com.jetbrains.rd.platform.util.getLogger
import java.awt.Image
import javax.imageio.ImageIO

class ImageHolder(private val imageContentSegment: ImageContentSegment) {
  companion object {
    private val logger = getLogger<ImageHolder>()
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


  private var cachedImage: Image? = null
  val image: Image?
    get() {
      val loadedImage = cachedImage

      @Suppress("FoldInitializerAndIfToElvis")
      if (loadedImage == null) {
        try {
          val reference = imageContentSegment.sourceReference as? FileReference ?: return null
          val image = ImageIO.read(reference.file ?: return null)
          cachedImage = image
          return image
        } catch (e: Exception) {
          logger.warn(e)
          return null
        }
      }

      return loadedImage
    }


  private var isInitialized = false

  private fun initializeIfNeeded() {
    if (isInitialized) return
    val image = this.image ?: return

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