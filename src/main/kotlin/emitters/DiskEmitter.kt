package emitters

import colors.ColorMixer
import colors.ColorPalette
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
        val palette = ColorPalette()

        for (i in 0 until starsCount) {
            val radius = maxRadius * sqrt(Random.nextDouble())
            val theta = Random.nextDouble() * 2.0 * PI

            val pos = Vector2(
                initialPosition.x + radius * cos(theta),
                initialPosition.y + radius * sin(theta)
            )
            val velocity = pos - initialPosition

            stars.add(createParticle(pos, velocity, palette))
        }

        return stars
    }

    private fun createParticle(initialPosition: Vector2, initialVelocity: Vector2, palette: ColorPalette): Particle {
        val velocityFactor = Random.nextDouble(25.0, 32.0)

        return when (variant) {
            0 -> Glitter(initialPosition, initialVelocity * velocityFactor, ColorMixer(palette, 0.25))
            1 -> Fish(initialPosition, initialVelocity * 0.6, ColorMixer(palette, 0.8))
            else -> Strobe(initialPosition, initialVelocity * velocityFactor * 1.1, ColorMixer(palette, 0.2))
        }
    }
}
