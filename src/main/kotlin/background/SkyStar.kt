package background

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import kotlin.random.Random

class SkyStar(private val x: Double, private val y: Double) : BackgroundProp {
    private val size = Random.nextInt(4)

    override fun draw(drawer: Drawer) {
        val radius = when(size) {
            0 -> 0.1
            1 -> 0.18
            2 -> 0.4
            3 -> 0.48
            else -> 0.1
        }

        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.fill = ColorRGBa.WHITE
        drawer.circle(x, y, radius)
    }
}
