package emitters

import colors.ColorMixer
import colors.ColorPalette
import particles.Glitter
import particles.Particle
import particles.Fish
import org.openrndr.math.Vector2
import particles.Strobe
import kotlin.random.Random

class RingEmitter : Emitter {
    private val variant = Random.nextInt(3)

    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Particle>()
        val starsCount = Random.nextInt(11, 23)
        val direction = Vector2(0.0, 1.0)
        val angle = 360.0 / starsCount
        val palette = ColorPalette()

        for (i in 0 until starsCount) {
            stars.add(createParticle(initialPosition, direction.rotate(angle * i), palette))
        }

        return stars
    }

    private fun createParticle(initialPosition: Vector2, direction: Vector2, palette: ColorPalette): Particle {
        val velocityFactor = Random.nextDouble(360.0, 390.0)

        return when (variant) {
            0 -> Glitter(initialPosition, direction * velocityFactor, ColorMixer(palette, 0.25))
            1 -> Fish(initialPosition, direction * velocityFactor / 70.0, ColorMixer(palette, 0.8))
            else -> Strobe(initialPosition, direction * velocityFactor, ColorMixer(palette, 0.2))
        }
    }
}
