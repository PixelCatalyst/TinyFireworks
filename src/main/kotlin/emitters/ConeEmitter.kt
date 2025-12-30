package emitters

import particles.Fish
import particles.Particle
import org.openrndr.math.Vector2
import particles.Glitter
import particles.Strobe
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class ConeEmitter : Emitter {
    private val variant = Random.nextInt(3)

    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Particle>()
        val starsCount = Random.nextInt(24, 37)
        val coneRadius = Random.nextDouble(0.45)

        for (i in 0 until starsCount) {
            val radius = 12.0 * sqrt(Random.nextDouble()) + 1.0
            val theta = Random.nextDouble() * PI * (1.0 - (2.0 * coneRadius)) + (PI * (1.0 + coneRadius))

            val pos = Vector2(
                initialPosition.x + radius * cos(theta),
                initialPosition.y + radius * sin(theta)
            )
            val velocity = pos - initialPosition

            stars.add(createParticle(pos, velocity))
        }

        return stars
    }

    private fun createParticle(initialPosition: Vector2, initialVelocity: Vector2): Particle {
        val velocityFactor = Random.nextDouble(360.0, 390.0)

        return when (variant) {
            0 -> Glitter(initialPosition, initialVelocity.normalized * velocityFactor)
            1 -> Fish(initialPosition, initialVelocity)
            else -> Strobe(initialPosition, initialVelocity.normalized * velocityFactor)
        }
    }
}
