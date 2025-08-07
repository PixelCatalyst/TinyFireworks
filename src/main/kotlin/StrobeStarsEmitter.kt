import org.openrndr.math.Vector2

class StrobeStarsEmitter : StarsEmitter {
    override fun emit(initialPosition: Vector2): Collection<Particle> {
        val stars = ArrayList<Strobe>()
        val partsCount = 17
        val direction = Vector2(0.0, 1.0)
        val angle = 360.0 / partsCount
        for (i in 0 until partsCount) {
            stars.add(Strobe(initialPosition, direction.rotate(angle * i) * 1780.0))
        }

        return stars
    }
}
