import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
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
    val fadeShaderFile = File("shaders/fade.glsl")
    val alphaChannelFragmentTransform = """
                    vec2 uv = c_boundsPosition.xy;
                    uv.y = 1.0 - uv.y;
                    vec4 color = texture(p_img, uv);
                    x_fill = color;
                """.trimIndent()

    class PixelizationFilter : Filter(filterShaderFromCode(pixelizeShaderFile.readText(), "pixelize")) {
        var pixelSize: Int by parameters

        init {
            pixelSize = 1
        }
    }

    class FadeFilter : Filter(filterShaderFromCode(fadeShaderFile.readText(), "fade"))

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

        val fadeFilter = FadeFilter()
        val pixelizationFilter = PixelizationFilter()
        pixelizationFilter.pixelSize = 5

        val background = loadImage("tmp_bg.png")

        val offscreenTarget = renderTarget(width, height) {
            colorBuffer()
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

            val offscreenColorBuffer = offscreenTarget.colorBuffer(0)
            fadeFilter.apply(offscreenColorBuffer, offscreenColorBuffer)
            drawer.isolatedWithTarget(offscreenTarget) {
                for (p in particles) {
                    p.draw(drawer)
                }
            }
            pixelizationFilter.apply(offscreenColorBuffer, offscreenColorBuffer)

            drawer.image(background)
            drawer.stroke = null
            drawer.shadeStyle = shadeStyle {
                fragmentTransform = alphaChannelFragmentTransform
                parameter("img", offscreenColorBuffer)
            }
            drawer.rectangle(drawer.bounds)
        }
    }
}
