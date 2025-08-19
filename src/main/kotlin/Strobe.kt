import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.times
import kotlin.random.Random

class Strobe(initialPos: Vector2, initialVelocity: Vector2) : Particle() {
    private var life = 0.63 + Random.nextDouble() * 0.22
    private var pos: Vector2 = initialPos
    private var velocity: Vector2 = initialVelocity
    private var acceleration: Vector2 = Vector2.ZERO

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val drag = 0.031
        val dragDeceleration = -0.5 * drag * velocity.squaredLength * velocity.normalized

        val deltaPosition = velocity * deltaSeconds + acceleration * (deltaSeconds * deltaSeconds * 0.5)
        pos += deltaPosition
        velocity += (acceleration + dragDeceleration) * (deltaSeconds * 0.5)
        acceleration = dragDeceleration

        life -= deltaSeconds
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
