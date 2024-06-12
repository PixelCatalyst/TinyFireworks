import org.openrndr.draw.Drawer

class Firework(private val x: Double, private val y: Double) {
    fun draw(drawer: Drawer) {
        drawer.circle(x, y, 40.0)
    }
}
