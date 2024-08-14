import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class Glitter(initialPos: Vector2, initialVelocity: Vector2) : Particle() {
    private var pos: Vector2 = initialPos
    private var velocity: Vector2 = initialVelocity

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val gravity = Vector2(0.0, 270.0)

        val deltaPosition = velocity * deltaSeconds + gravity * (deltaSeconds * deltaSeconds * 0.5)
        pos += deltaPosition
        velocity += gravity * deltaSeconds

        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return pos.y > 1000.0
    }

    override fun draw(drawer: Drawer) {
        drawer.circle(pos, 6.0)
    }
}