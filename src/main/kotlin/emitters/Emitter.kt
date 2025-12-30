package emitters

import particles.Particle
import org.openrndr.math.Vector2

interface Emitter {
    fun emit(initialPosition: Vector2): Collection<Particle>
}
