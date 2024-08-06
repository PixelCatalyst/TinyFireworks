import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import kotlin.random.Random

fun main() = application {
    configure {
        width = 640
        height = 480
        windowResizable = true
    }

    val particles = ArrayList<Particle>()

    var lastSeconds = 0.0

    fun fireFirework() {
        particles.add(
            PeonyFirework(
                Random.nextDouble() * (configuration.width - 100.0) + 50.0,
                configuration.height.toDouble()
            )
        )
    }

    program {

        keyboard.keyDown.listen {
            if (it.key == KEY_SPACEBAR) {
                fireFirework()
            }
        }

        fireFirework()

        extend {
            drawer.clear(ColorRGBa.BLACK)

            val emittedParticles = ArrayList<Particle>()
            for (p in particles) {
                emittedParticles.addAll(p.update(seconds - lastSeconds))
                p.draw(drawer)
            }
            particles.removeAll { it.hasExpired() }
            particles.addAll(emittedParticles)

            lastSeconds = seconds
        }
    }
}
