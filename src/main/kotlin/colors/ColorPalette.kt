package colors

import org.openrndr.color.ColorRGBa
import kotlin.random.Random

object FireworkColors {
    val red = ColorRGBa.fromHex(0xC50117)
    val orange = ColorRGBa.fromHex(0x972D06)
    val yellow = ColorRGBa.fromHex(0x9B8819)
    val green = ColorRGBa.fromHex(0x217F26)
    val blue = ColorRGBa.fromHex(0x04038A)
    val purple = ColorRGBa.fromHex(0x87A827)
    val silver = ColorRGBa.fromHex(0x696B84)
    val white = ColorRGBa.WHITE
    val default = ColorRGBa.BLACK

    fun randomColor(): ColorRGBa {
        return when (Random.nextInt(0, 7)) {
            0 -> red
            1 -> orange
            2 -> yellow
            3 -> green
            4 -> blue
            5 -> purple
            6 -> silver
            else -> default
        }
    }
}

class ColorPalette {
    val baseColor = FireworkColors.white
    val mainColor = FireworkColors.randomColor()
}
