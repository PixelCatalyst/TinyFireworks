import org.openrndr.color.ColorRGBa
import kotlin.random.Random

fun nextColor(): ColorRGBa {
    return when (Random.nextInt(0, 8)) {
        0 -> ColorRGBa.RED
        1 -> ColorRGBa.YELLOW
        2 -> ColorRGBa.GREEN
        3 -> ColorRGBa.BLUE
        4 -> ColorRGBa.PINK
        5 -> ColorRGBa.CYAN
        6 -> ColorRGBa.WHITE
        7 -> ColorRGBa.MAGENTA
        else -> ColorRGBa.BLACK
    }
}
