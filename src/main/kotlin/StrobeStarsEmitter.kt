import org.openrndr.math.Vector2
import kotlin.math.*
import kotlin.random.Random

class StrobeStarsEmitter : StarsEmitter {
    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Strobe>()
        val starsCount = 77

        for (i in 0 until starsCount) {
            val radius = 50.0 * sqrt(Random.nextDouble())
            val theta = Random.nextDouble() * 2 * PI

            val pos = Vector2(
                initialPosition.x + radius * cos(theta),
                initialPosition.y + radius * sin(theta)
            )
            val velocity = pos - initialPosition

            stars.add(Strobe(pos, velocity * 25.0))
        }

        return stars
    }
}
