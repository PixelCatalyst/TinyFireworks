package particles

import colors.ColorMixer
import emitters.BurstEmitter
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.times
import kotlin.random.Random

class Strobe(initialPos: Vector2, initialVelocity: Vector2, private val color: ColorMixer) : Particle() {
    private var life = 0.63 + Random.nextDouble() * 0.22
    private var pos: Vector2 = initialPos
    private var velocity: Vector2 = initialVelocity
    private var acceleration: Vector2 = Vector2.ZERO

    override fun update(deltaSeconds: Double): Collection<Particle> {
        color.update(deltaSeconds)

        val drag = 0.16
        val dragDeceleration = -0.5 * drag * velocity.squaredLength * velocity.normalized

        val deltaPosition = velocity * deltaSeconds + acceleration * (deltaSeconds * deltaSeconds * 0.5)
        pos += deltaPosition
        velocity += (acceleration + dragDeceleration) * (deltaSeconds * 0.5)
        acceleration = dragDeceleration

        life -= deltaSeconds
        if (life <= 0.0) {
            return BurstEmitter().emit(pos) + Flash(pos, 0.0)
        }
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return life <= 0.0
    }

    override fun blur(): String {
        return "small"
    }

    override fun draw(drawer: Drawer) {
        val radius = 0.8
        drawer.fill = color.get()
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.circle(pos, radius)
        drawer.fill = color.get().times(2.0)
        drawer.circle(pos, radius / 3.0)
    }
}
