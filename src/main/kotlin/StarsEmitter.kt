import org.openrndr.math.Vector2

interface StarsEmitter {
    fun emit(initialPosition: Vector2): Collection<Particle>
}
