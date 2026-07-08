package background

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.random.Random

private fun nextRoof(): Roof? {
    return when (Random.nextInt(100)) {
        in 0..10 -> LeftSlantedRoof()
        in 10..20 -> RightSlantedRoof()
        in 20..50 -> BulkheadRoof()
        in 50..70 -> AntennaRoof()
        in 70..85 -> BulkheadAntennaRoof()
        else -> null
    }
}

class CityBuilding(private var bottomLeftCorner: Vector2) : BackgroundProp {
    val width = Random.nextInt(10, 24).toDouble()

    private var roof = nextRoof()

    private val trunkHeight = Random.nextInt(15, 55).toDouble()
    val height
        get() = trunkHeight + (roof?.getHeight() ?: 0.0)

    fun shiftRight(x: Double) {
        bottomLeftCorner += Vector2(x, 0.0)
    }

    override fun draw(drawer: Drawer) {
        val topLeftCorner = bottomLeftCorner - Vector2(0.0, trunkHeight)

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.rectangle(Rectangle(topLeftCorner, width, trunkHeight))

        roof?.draw(topLeftCorner, width, drawer)
    }
}
