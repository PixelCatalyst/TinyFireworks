package emitters

import particles.Fish
import particles.Particle
import org.openrndr.math.Vector2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class FishStarsEmitter : StarsEmitter {
    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Fish>()
        val starsCount = 33

        for (i in 0 until starsCount) {
            val radius = 12.0 * sqrt(Random.nextDouble()) + 1.0
            val theta = Random.nextDouble() * (PI * 0.6) + (PI * 1.2)

            val pos = Vector2(
                initialPosition.x + radius * cos(theta),
                initialPosition.y + radius * sin(theta)
            )
            val velocity = pos - initialPosition

            stars.add(Fish(pos, velocity))
        }

        return stars
    }
}
