package background

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.shape.Triangle
import kotlin.random.Random

interface Roof {
    fun draw(origin: Vector2, width: Double, drawer: Drawer)

    fun getHeight(): Double
}

class LeftSlantedRoof : Roof {
    private val height = Random.nextInt(5, 16).toDouble()

    override fun draw(origin: Vector2, width: Double, drawer: Drawer) {
        val roofShape = Triangle(
            origin,
            origin + Vector2(width, 0.0),
            origin + Vector2(width, -height)
        )

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.contour(roofShape.contour)
    }

    override fun getHeight(): Double {
        return height
    }
}

class RightSlantedRoof : Roof {
    private val height = Random.nextInt(5, 16).toDouble()

    override fun draw(origin: Vector2, width: Double, drawer: Drawer) {
        val roofShape = Triangle(
            origin + Vector2(0.0, -height),
            origin,
            origin + Vector2(width, 0.0)
        )

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.contour(roofShape.contour)
    }

    override fun getHeight(): Double {
        return height
    }
}

class BulkheadRoof : Roof {
    private val height = Random.nextInt(3, 11).toDouble()
    private val widthScale = Random.nextDouble(0.4, 0.8)

    override fun draw(origin: Vector2, width: Double, drawer: Drawer) {
        val bulkheadWidth = width * widthScale
        val x = origin.x + (width - bulkheadWidth) / 2.0
        val y = origin.y - height

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.rectangle(x, y, bulkheadWidth, height)
    }

    override fun getHeight(): Double {
        return height
    }
}

class AntennaRoof : Roof {
    private val baseHeight = Random.nextInt(2, 6).toDouble()
    private val poleHeight = Random.nextInt(9, 16).toDouble()
    private val poleWidth = Random.nextInt(1, 3).toDouble()

    private val widthScale = Random.nextDouble(0.2, 0.35)
    private val marginFactor = Random.nextDouble(1.1, 8.0)

    override fun draw(origin: Vector2, width: Double, drawer: Drawer) {
        val baseWidth = width * widthScale
        val baseX = origin.x + (width - baseWidth) / marginFactor
        val baseY = origin.y - baseHeight

        val poleX = baseX + (baseWidth - poleWidth) / 2.0
        val poleY = baseY - poleHeight

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.rectangle(baseX, baseY, baseWidth, baseHeight)
        drawer.rectangle(poleX, poleY, poleWidth, poleHeight)
    }

    override fun getHeight(): Double {
        return baseHeight + poleHeight
    }
}

class BulkheadAntennaRoof : Roof {
    private val bulkheadHeight = Random.nextInt(2, 8).toDouble()
    private val baseHeight = Random.nextInt(2, 5).toDouble()
    private val poleHeight = Random.nextInt(10, 15).toDouble()
    private val poleWidth = Random.nextInt(1, 3).toDouble()

    private val widthScale = Random.nextDouble(0.5, 0.7)

    override fun draw(origin: Vector2, width: Double, drawer: Drawer) {
        val bulkheadWidth = width * widthScale
        val bulkheadX = origin.x + (width - bulkheadWidth) / 2.0
        val bulkheadY = origin.y - bulkheadHeight

        val baseWidth = bulkheadWidth * (widthScale * 0.8)
        val baseX = bulkheadX + (bulkheadWidth - baseWidth) / 2.0
        val baseY = bulkheadY - baseHeight

        val poleX = baseX + (baseWidth - poleWidth) / 2.0
        val poleY = baseY - poleHeight

        drawer.fill = ColorRGBa.BLACK
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.rectangle(bulkheadX, bulkheadY, bulkheadWidth, bulkheadHeight)
        drawer.rectangle(baseX, baseY, baseWidth, baseHeight)
        drawer.rectangle(poleX, poleY, poleWidth, poleHeight)
    }

    override fun getHeight(): Double {
        return bulkheadHeight + baseHeight + poleHeight
    }
}
