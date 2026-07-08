package background

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.random.Random

class BuildingSeparator(private val bottomLeftCorner: Vector2) : BackgroundProp {
    val width = Random.nextInt(4, 10).toDouble()
    val height = Random.nextInt(5, 12).toDouble()

    override fun draw(drawer: Drawer) {
        val topLeftCorner = bottomLeftCorner + Vector2(0.0, -height)

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.rectangle(Rectangle(topLeftCorner, width, height))
    }
}
