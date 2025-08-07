import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

class PeonyFirework(initialX: Double, initialY: Double, private var fuel: Double, private var emitter: StarsEmitter) :
    Particle() {
    private var pos: Vector2 = Vector2(initialX, initialY)
    private var velocity: Vector2 = Vector2(0.0)
    private var acceleration: Vector2 = Vector2(0.0)
    private var emitted = false

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val mass = 1.5
        val burnRate = 120.0
        val gravity = Vector2(0.0, 670.0)

        val thrust =
            if (fuel > 0.0) Vector2(0.0, -2210.0)
            else Vector2(0.0)
        fuel -= burnRate * deltaSeconds

        val deltaPosition = velocity * deltaSeconds + acceleration * (deltaSeconds * deltaSeconds * 0.5)
        pos += deltaPosition
        val newAcceleration = (thrust / mass) + gravity
        velocity += (acceleration + newAcceleration) * (deltaSeconds * 0.5)
        acceleration = newAcceleration

        if (fuel <= 0.0 && deltaPosition.y >= 0.0 && !emitted) {
            emitted = true
            return emitter.emit(pos)
        }
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return emitted
    }

    override fun blur(): String {
        return "large"
    }

    override fun draw(drawer: Drawer) {
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.circle(pos, 8.0)
    }
}
