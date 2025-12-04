package particles

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.random.Random

class Fish(initialPos: Vector2, initialVelocity: Vector2) : Particle() {
    private var life = Random.nextDouble(0.8, 1.9)
    private var pos: Vector2 = initialPos
    private var velocity: Vector2 = initialVelocity * Random.nextDouble(2.3, 3.5)

    private var turns = Random.nextInt(3, 6)
    private var turnChance = Random.nextDouble(80.0, 90.0)
    private var turnAngle = Random.nextDouble(20.0, 33.0)

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val deltaPosition = velocity * deltaSeconds
        pos += deltaPosition

        if (turns > 0 && Random.nextDouble(100.0) < turnChance) {
            val sign = if (Random.nextBoolean()) 1.0 else -1.0
            val degrees = Random.nextDouble(turnAngle / 2.0, turnAngle)
            velocity = velocity.rotate(sign * degrees)
            velocity *= 1.31

            --turns
            turnChance = 0.0
            turnAngle += Random.nextDouble(7.0, 12.0)
        } else if (turns > 0) {
            turnChance += (50.0 + turns * 3.5) * deltaSeconds
        }

        val lifeMultiplier =
            if (velocity.y > 0) 1.66
            else 1.0
        life -= deltaSeconds * lifeMultiplier
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return life <= 0.0
    }

    override fun blur(): String {
        return "small"
    }

    override fun draw(drawer: Drawer) {
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.TRANSPARENT
        drawer.circle(pos, 1.0)
    }
}
