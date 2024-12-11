import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import java.io.File
import kotlin.math.min
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1280
        height = 880
        windowResizable = true
    }

    val pixelizeShaderFile = File("shaders/pixelize.glsl")

    class PixelizationFilter : Filter(filterShaderFromCode(pixelizeShaderFile.readText(), "pixelize")) {
        var pixelSize: Int by parameters

        init {
            pixelSize = 1
        }
    }

    val particles = ArrayList<Particle>()

    var lastSeconds: Double? = null

    fun fireFirework() {
        particles.add(
            PeonyFirework(
                Random.nextDouble() * (configuration.width - 100.0) + 50.0,
                configuration.height.toDouble(),
                100.0
            )
        )
    }

    program {

        keyboard.keyDown.listen {
            if (it.key == KEY_SPACEBAR) {
                fireFirework()
            }
        }

        val pixelizationFilter = PixelizationFilter()
        pixelizationFilter.pixelSize = 5

        val offscreenTarget = renderTarget(width, height) {
            colorBuffer()
            depthBuffer()
        }

        fireFirework()

        extend {
            val timeStep = 1.0 / 60.0
            val maxStepsPerFrame = 20
            lastSeconds = lastSeconds ?: seconds

            var frameSeconds = seconds - lastSeconds!!
            if ((frameSeconds / timeStep) > maxStepsPerFrame) {
                frameSeconds = 0.0
            }
            lastSeconds = seconds

            while (frameSeconds > 0.0) {
                val deltaSeconds = min(frameSeconds, timeStep)

                val emittedParticles = ArrayList<Particle>()
                for (p in particles) {
                    emittedParticles.addAll(p.update(deltaSeconds))
                }
                particles.removeAll { it.hasExpired() }
                particles.addAll(emittedParticles)

                frameSeconds -= deltaSeconds
            }

            drawer.isolatedWithTarget(offscreenTarget) {
                clear(ColorRGBa.BLACK)
                for (p in particles) {
                    p.draw(drawer)
                }
            }

            val offscreenColorBuffer = offscreenTarget.colorBuffer(0)
            pixelizationFilter.apply(offscreenColorBuffer, offscreenColorBuffer)

            drawer.image(offscreenColorBuffer)
        }
    }
}
