import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.random.Random

class Flash(initialPos: Vector2, delay: Double = Random.nextDouble(0.0, 1.5)) : Particle() {
    private val size = Random.nextDouble(7.0, 16.0)
    private val flashDuration = Random.nextDouble(0.08, 0.234)

    private var life = delay + flashDuration
    private var pos: Vector2 = initialPos

    override fun update(deltaSeconds: Double): Collection<Particle> {
        life -= deltaSeconds
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return life <= 0.0
    }

    override fun blur(): String {
        return "large"
    }

    override fun draw(drawer: Drawer) {
        if ((life - flashDuration) < 0.0) {
            drawer.stroke = ColorRGBa.TRANSPARENT

            val baseColor = ColorRGBa.WHITE

            val outerBandColor = ColorRGBa(baseColor.r, baseColor.g, baseColor.b, 0.01)
            drawer.fill = outerBandColor
            drawer.circle(pos, size * 1.7)

            val innerBandColor = ColorRGBa(baseColor.r, baseColor.g, baseColor.b, 0.1)
            drawer.fill = innerBandColor
            drawer.circle(pos, size)

            drawer.fill = baseColor
            drawer.circle(pos, size * 0.5)
        }
    }
}
