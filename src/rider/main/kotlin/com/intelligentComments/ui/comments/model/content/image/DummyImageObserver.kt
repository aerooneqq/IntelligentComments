package com.intelligentComments.ui.comments.model.content.image

import java.awt.Image
import java.awt.image.ImageObserver

class DummyImageObserver : ImageObserver {
    companion object {
        val instance = DummyImageObserver()
    }

    override fun imageUpdate(img: Image?, infoflags: Int, x: Int, y: Int, width: Int, height: Int): Boolean = false
}