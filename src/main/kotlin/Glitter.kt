import org.openrndr.draw.Drawer

class Glitter(private val x: Double, private var y: Double) : Particle() {
    override fun update(deltaSeconds: Double): Collection<Particle> {
        // TODO
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return false
    }

    override fun draw(drawer: Drawer) {
        drawer.circle(x, y, 16.0)
    }
}