import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.times
import kotlin.random.Random

class Glitter(initialPos: Vector2, initialVelocity: Vector2) : Particle() {
    private val gravity = Vector2(0.0, 670.0)

    private var life = 0.45 + Random.nextDouble() * 0.1
    private var pos: Vector2 = initialPos
    private var velocity: Vector2 = initialVelocity
    private var acceleration: Vector2 = gravity

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val drag = 0.033
        val dragDeceleration = -0.5 * drag * velocity.squaredLength * velocity.normalized
        val gravityDragCoef = 0.5

        val deltaPosition = velocity * deltaSeconds + acceleration * (deltaSeconds * deltaSeconds * 0.5)
        pos += deltaPosition
        val newAcceleration = gravity * gravityDragCoef + dragDeceleration
        velocity += (acceleration + newAcceleration) * (deltaSeconds * 0.5)
        acceleration = newAcceleration

        life -= deltaSeconds
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return life <= 0.0
    }

    override fun draw(drawer: Drawer) {
        val cyan = ColorRGBa.CYAN
        val alpha =
            if (life > 0.2) 1.0
            else 5 * life - life * life
        val fillColor = ColorRGBa(cyan.r, cyan.g, cyan.b, alpha)
        drawer.fill = fillColor
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.circle(pos, 6.0)
    }
}
