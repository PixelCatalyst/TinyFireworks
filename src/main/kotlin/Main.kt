import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Filter
import org.openrndr.draw.filterShaderFromCode
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import kotlin.math.min
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1280
        height = 880
        windowResizable = true
    }

    val postFilterShader = """
        #version 330
        // -- part of the filter interface, every filter has these
        in vec2 v_texCoord0;
        uniform sampler2D tex0;
        out vec4 o_color;

        // -- user parameters
        uniform float time;

        void main() {
            vec4 color = texture(tex0, v_texCoord0);
            color.b = 0.0;
            o_color = color;
        }
        """

    class PostFilter : Filter(filterShaderFromCode(postFilterShader, "post-filter-shader")) {
        var time: Double by parameters

        init {
            time = 0.0
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

        val postFilter = PostFilter()

        val offscreenTarget = renderTarget(width, height) {
            colorBuffer()
            depthBuffer()
        }

        fireFirework()

        extend {
            // TODO(WIP) shader experiment

            val timeStep = 1.0 / 60.0
            val maxStepsPerFrame = 20
            lastSeconds = lastSeconds ?: seconds

            var frameSeconds = seconds - lastSeconds!!
            if ((frameSeconds / timeStep) > maxStepsPerFrame) {
                frameSeconds = 0.0
            }
            println(frameSeconds)
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

            postFilter.time = seconds
            val offscreenColorBuffer = offscreenTarget.colorBuffer(0)
            postFilter.apply(offscreenColorBuffer, offscreenColorBuffer)

            drawer.image(offscreenColorBuffer)
        }
    }
}
