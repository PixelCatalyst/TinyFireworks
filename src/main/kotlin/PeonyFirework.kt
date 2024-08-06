import org.openrndr.draw.Drawer
import kotlin.math.abs

class PeonyFirework(initialX: Double, private val initialY: Double, private val reach: Double) : Particle() {
    private var x: Double = initialX
    private var y: Double = initialY

    private var emitted = false

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val speed = 200.0

        y -= speed * deltaSeconds
        if (abs(y - initialY) >= reach && !emitted) {
            emitted = true
            return listOf(Glitter(x, y))
        }
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return emitted
    }

    override fun draw(drawer: Drawer) {
        drawer.circle(x, y, 8.0)
    }
}
