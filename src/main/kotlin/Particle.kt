import org.openrndr.draw.Drawer

abstract class Particle {
    abstract fun update(deltaSeconds: Double): Collection<Particle>

    abstract fun hasExpired(): Boolean

    abstract fun blur(): String

    abstract fun draw(drawer: Drawer)
}
