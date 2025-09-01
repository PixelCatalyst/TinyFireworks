import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import java.io.File
import kotlin.math.min
import kotlin.random.Random

data class MotionBlurTarget(val factor: Double, val target: RenderTarget)

fun main() = application {
    configure {
        width = 1280
        height = 720
        windowResizable = true
    }

    val canvasWidth = 320
    val canvasHeight = 180

    val alphaChannelFragmentTransform = """
                    vec2 uv = c_boundsPosition.xy;
                    uv.y = 1.0 - uv.y;
                    vec4 color = texture(p_img, uv);
                    x_fill = color;
                """.trimIndent()

    val pixelizeShaderFile = File("shaders/pixelize.glsl")
    val fadeShaderFile = File("shaders/fade.glsl")

    class PixelizationFilter : Filter(filterShaderFromCode(pixelizeShaderFile.readText(), "pixelize")) {
        var pixelSize: Int by parameters

        init {
            pixelSize = 1
        }
    }

    class FadeFilter : Filter(filterShaderFromCode(fadeShaderFile.readText(), "fade")) {
        var factor: Double by parameters

        init {
            factor = 0.0
        }
    }

    val particles = ArrayList<Particle>()

    var lastSeconds: Double? = null

    fun fireFirework() {
        particles.add(
            PeonyFirework(
                Random.nextDouble() * (canvasWidth - 100.0) + 50.0,
                canvasHeight.toDouble(),
                85.0,
                if (Random.nextDouble() > 0.5) StrobeStarsEmitter() else GlitterStarsEmitter()
            )
        )
    }

    fun drawBufferWithAlpha(drawer: Drawer, colorBuffer: ColorBuffer) {
        drawer.isolated {
            drawer.stroke = null
            drawer.shadeStyle = shadeStyle {
                fragmentTransform = alphaChannelFragmentTransform
                parameter("img", colorBuffer)
            }
            drawer.rectangle(drawer.bounds)
        }
    }

    program {
        val mainTarget = renderTarget(canvasWidth, canvasHeight) { colorBuffer() }
        mainTarget.colorBuffer(0).filter(MinifyingFilter.NEAREST, MagnifyingFilter.NEAREST)

        val motionBlurCategories = mapOf(
            "none" to MotionBlurTarget(0.0, renderTarget(mainTarget.width, mainTarget.height) { colorBuffer() }),
            "small" to MotionBlurTarget(0.8, renderTarget(mainTarget.width, mainTarget.height) { colorBuffer() }),
            "large" to MotionBlurTarget(0.95, renderTarget(mainTarget.width, mainTarget.height) { colorBuffer() }),
        )

        keyboard.keyDown.listen {
            if (it.key == KEY_SPACEBAR) {
                fireFirework()
            }
        }

        val fadeFilter = FadeFilter()
        val pixelizationFilter = PixelizationFilter()
        pixelizationFilter.pixelSize = 5

        val background = loadImage("tmp_bg.png")

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

            for (mbc in motionBlurCategories) {
                val target = mbc.value.target
                val targetBuffer = target.colorBuffer(0)

                fadeFilter.factor = mbc.value.factor
                fadeFilter.apply(targetBuffer, targetBuffer)
                drawer.isolatedWithTarget(target) {
                    ortho(target)

                    for (p in particles) {
                        if (p.blur() == mbc.key) {
                            p.draw(drawer)
                        }
                    }
                }
            }

            drawer.isolatedWithTarget(mainTarget) {
                ortho(mainTarget)

                drawer.image(background, Vector2.ZERO, canvasWidth.toDouble(), canvasHeight.toDouble())
                for (mbc in motionBlurCategories) {
                    drawBufferWithAlpha(drawer, mbc.value.target.colorBuffer(0))
                }
            }

            drawer.image(mainTarget.colorBuffer(0), Vector2.ZERO, width.toDouble(), height.toDouble())
        }
    }
}
