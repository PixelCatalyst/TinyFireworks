import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class Strobe(initialPos: Vector2, initialVelocity: Vector2) : Particle() {
    private var life = 0.45
    private var pos: Vector2 = initialPos
    private var velocity: Vector2 = initialVelocity

    override fun update(deltaSeconds: Double): Collection<Particle> {
        pos += velocity * deltaSeconds * 0.1
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return life <= 0.0
    }

    override fun blur(): String {
        return "none"
    }

    override fun draw(drawer: Drawer) {
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.circle(pos, 4.0)
    }
}
