import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.random.Random

class PeonyFirework(initialX: Double, initialY: Double, private var emitter: StarsEmitter) :
    Particle() {
    private var pos: Vector2 = Vector2(initialX, initialY)
    private var velocity: Vector2 = Vector2(0.0)
    private var acceleration: Vector2 = Vector2(0.0)

    private val accelerationForce: Vector2 = Vector2(0.0, -460.0 + Random.nextDouble(-5.0, 5.0))
    private val decelerationForce: Vector2 = Vector2(0.0, 215.0 + Random.nextDouble(-5.0, 5.0))

    private val ascendingLife = 0.4 + Random.nextDouble(-0.06, 0.05)
    private val descendingLife = 0.8 + Random.nextDouble(-0.05, 0.015)
    private var life = ascendingLife + descendingLife

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val deltaPosition = velocity * deltaSeconds + acceleration * (deltaSeconds * deltaSeconds * 0.5)
        pos += deltaPosition
        val newAcceleration = if ((life - descendingLife) <= 0.0) decelerationForce else accelerationForce
        velocity += (acceleration + newAcceleration) * (deltaSeconds * 0.5)
        acceleration = newAcceleration

        life -= deltaSeconds
        if (life <= 0.0) {
            return emitter.emit(pos)
        }
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return life <= 0.0
    }

    override fun blur(): String {
        return "large"
    }

    override fun draw(drawer: Drawer) {
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.circle(pos, 2.0)
    }
}
