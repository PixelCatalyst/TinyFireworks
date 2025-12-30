package emitters

import particles.Particle
import particles.Strobe
import org.openrndr.math.Vector2
import particles.Fish
import particles.Glitter
import kotlin.math.*
import kotlin.random.Random

class DiskEmitter : Emitter {
    private val variant = Random.nextInt(3)

    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Particle>()
        val starsCount = Random.nextInt(60, 80)
        val maxRadius = Random.nextDouble(8.0, 12.0)

        for (i in 0 until starsCount) {
            val radius = maxRadius * sqrt(Random.nextDouble())
            val theta = Random.nextDouble() * 2.0 * PI

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
        val velocityFactor = Random.nextDouble(25.0, 32.0)

        return when (variant) {
            0 -> Glitter(initialPosition, initialVelocity * velocityFactor)
            1 -> Fish(initialPosition, initialVelocity * 0.6)
            else -> Strobe(initialPosition, initialVelocity * velocityFactor * 1.1)
        }
    }
}
