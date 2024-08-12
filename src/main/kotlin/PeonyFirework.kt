import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class PeonyFirework(initialX: Double, initialY: Double, private var fuel: Double) : Particle() {
    private var pos: Vector2 = Vector2(initialX, initialY)
    private var velocity: Vector2 = Vector2(1.0)

    private var emitted = false

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val mass = 1.5;
        val burnRate = 120.0;

        val gravity = Vector2(0.0, 1000.0)
        val thrust =
            if (fuel > 0.0) Vector2(0.0, -2200.0)
            else Vector2(0.0)
        fuel -= burnRate * deltaSeconds

        val force = thrust + gravity
        val acceleration = force / mass
        velocity += acceleration * deltaSeconds
        val deltaPosition = velocity * deltaSeconds
        pos += deltaPosition

        if (fuel <= 0.0 && deltaPosition.y >= 0.0 && !emitted) {
            emitted = true
            return listOf(Glitter(pos.x, pos.y))
        }
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return emitted
    }

    override fun draw(drawer: Drawer) {
        drawer.circle(pos, 8.0)
    }
}
