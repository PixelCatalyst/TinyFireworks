import org.openrndr.math.Vector2
import kotlin.random.Random

class FlashStarsEmitter : StarsEmitter {
    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Flash>()
        val starsCount = Random.nextInt(1, 4)
        val direction = Vector2(0.0, 1.0).rotate(Random.nextDouble(0.0, 360.0))
        val angle = 360.0 / starsCount
        for (i in 0 until starsCount) {
            val translation = direction.rotate(angle * i) * Random.nextDouble(10.0, 48.0)
            stars.add(Flash(initialPosition + translation))
        }

        return stars
    }
}
