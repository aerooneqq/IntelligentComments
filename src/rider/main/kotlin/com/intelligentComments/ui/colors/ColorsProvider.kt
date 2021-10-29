package com.intelligentComments.ui.colors

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.diagnostics.logAssertion
import java.awt.Color
import java.util.*

data class ColorName(val name: String)

object Colors {
    val EmptyColor = ColorName("")

    val LeftLineBackgroundColor = ColorName("left.default.comment.line.background")

    val AddNewInvariantBorderColor = ColorName("add.new.invariant.border.color")
    val AddNewInvariantBorderHoveredColor = ColorName("add.new.invariant.border.hover.color")
    val InvariantDefaultBorderColor = ColorName("text.invariant.border.default.background")
    val TextInvariantBackgroundColor = ColorName("text.invariant.background")
    val TextInvariantHoveredBackgroundColor = ColorName("text.invariant.background.hovered")

    val ReferenceHeaderBackgroundColor = ColorName("reference.header.background")
    val ReferenceHeaderHoveredBackgroundColor = ColorName("reference.header.background.hovered")

    val TextDefaultColor = ColorName("text.default.color")
    val TextDefaultHoveredColor = ColorName("text.default.color.hovered")

    val ListItemBulletBackground = ColorName("list.item.bullet.background.color")

    val TableBorderBackground = ColorName("table.border.background.color")
    val TableHeaderCellBackground = ColorName("table.header.cell.background.color")
}

interface ColorsProvider {
    fun getColorFor(colorName: ColorName): Color
}

class ColorsProviderImpl(project: Project) : ColorsProvider {
    companion object {
        val logger = Logger.getInstance(ColorsProviderImpl::class.java)
        val fallbackColor: Color = Color.PINK
    }

    private val properties = Properties()
    private val decodedColors = HashMap<String, Color>()


    override fun getColorFor(colorName: ColorName): Color {
        loadColorsIfNeeded()
        val hexString = properties.getProperty(colorName.name)
        if (hexString == null) {
            logger.logAssertion("Color hex string was null")
            return fallbackColor
        }

        var existingColor = decodedColors[colorName.name]
        if (existingColor == null) {
            existingColor = Color.decode(properties.getProperty(colorName.name))
            decodedColors[colorName.name] = existingColor
            return existingColor
        }

        return existingColor
    }

    private fun loadColorsIfNeeded() {
        if (properties.isEmpty) {
            properties.load(javaClass.classLoader.getResourceAsStream("colors/DarkColors.properties"))
        }
    }
}