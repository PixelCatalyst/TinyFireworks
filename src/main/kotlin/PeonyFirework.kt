import org.openrndr.draw.Drawer

class PeonyFirework(private val x: Double, private var y: Double) : Particle() {

    private var emitted = false

    override fun update(deltaSeconds: Double): Collection<Particle> {
        val speed = 200.0

        y -= speed * deltaSeconds
        if (y < 200 && !emitted) {
            emitted = true
            return listOf(Glitter(x, y))
        }
        return emptyList()
    }

    override fun hasExpired(): Boolean {
        return emitted
    }

    override fun draw(drawer: Drawer) {
        drawer.circle(x, y, 8.0)
    }
}
