import background.BackgroundProp
import background.BuildingSeparator
import background.CityBuilding
import background.SkyStar
import org.openrndr.KEY_ENTER
import org.openrndr.KEY_ESCAPE
import org.openrndr.KEY_SPACEBAR
import org.openrndr.Window
import org.openrndr.application
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import particles.Particle
import particles.Firework
import java.io.File
import kotlin.math.*
import kotlin.random.Random

data class MotionBlurTarget(val factor: Double, val target: RenderTarget)

fun main() = application {
    configure {
        width = 1280
        height = 720
        windowResizable = true
    }

    var isFullscreen = false
    var lastWindowSize = Vector2(1280.0, 720.0)
    var lastWindowPosition = Vector2(0.0, 0.0)

    val canvasWidth = 320
    val canvasHeight = 180

    val alphaChannelFragmentTransform = """
                    vec2 uv = c_boundsPosition.xy;
                    uv.y = 1.0 - uv.y;
                    vec4 color = texture(p_img, uv);
                    x_fill = color;
                """.trimIndent()

    val fadeShaderFile = File("shaders/fade.glsl")
    val gradientShaderFile = File("shaders/gradient.glsl")

    class FadeFilter : Filter(filterShaderFromCode(fadeShaderFile.readText(), "fade")) {
        var factor: Double by parameters

        init {
            factor = 0.0
        }
    }

    class GradientFilter : Filter(filterShaderFromCode(gradientShaderFile.readText(), "gradient")) {
        var colorTop: Vector3 by parameters
        var colorBottom: Vector3 by parameters

        init {
            colorTop = Vector3(0.03, 0.0, 0.17)
            colorBottom = Vector3(0.22, 0.2, 0.35)
        }
    }

    val particles = ArrayList<Particle>()

    var lastSeconds: Double? = null

    fun generateBackgroundStars(): Collection<BackgroundProp> {
        val count = 32
        val segmentWidth = canvasWidth / count.toDouble()
        val segmentOffset = segmentWidth / 5.0
        val starProps = ArrayList<BackgroundProp>()

        for (i in 0 until count) {
            val countPerSegment = Random.nextInt(3)
            (0 until countPerSegment).forEach {
                starProps.add(
                    SkyStar(
                        segmentWidth * i + Random.nextDouble(segmentOffset, segmentWidth - segmentOffset),
                        Random.nextDouble(4.0, canvasHeight / 2.3)
                    )
                )
            }
        }

        return starProps
    }

    fun generateBackgroundCity(): Collection<BackgroundProp> {
        val buildingProps = ArrayList<BackgroundProp>()

        var i = 0
        var bottomLeftCorner = Vector2(0.0, canvasHeight.toDouble())
        var prevBuildingHeight = 0.0
        while (i < canvasWidth) {
            val building = CityBuilding(bottomLeftCorner)

            if (abs(prevBuildingHeight - building.height) < 9.0) {
                val separator = BuildingSeparator(bottomLeftCorner)
                building.shiftRight(separator.width)

                buildingProps.add(separator)
                i += separator.width.toInt()
                bottomLeftCorner += Vector2(separator.width, 0.0)
            }

            buildingProps.add(building)
            i += building.width.toInt()
            bottomLeftCorner += Vector2(building.width, 0.0)

            prevBuildingHeight = building.height
        }

        return buildingProps
    }

    fun generateBackgroundImage(drawer: Drawer, target: RenderTarget, filter: Filter) {
        val background = target.colorBuffer(0)
        filter.apply(background, background)

        val backgroundProps = ArrayList<BackgroundProp>()
        backgroundProps.addAll(generateBackgroundStars())
        backgroundProps.addAll(generateBackgroundCity())

        drawer.isolatedWithTarget(target) {
            ortho(target)

            for (bp in backgroundProps) {
                bp.draw(drawer)
            }
        }
    }

    fun fireFirework() {
        particles.add(
            Firework(
                Random.nextDouble() * (canvasWidth - 100.0) + 50.0,
                (canvasHeight + 8).toDouble()
            )
        )
    }

    fun toggleFullscreen(window: Window) {
        if (isFullscreen) {
            isFullscreen = false

            window.size = lastWindowSize
            window.position = lastWindowPosition
        } else {
            isFullscreen = true

            lastWindowSize = window.size
            lastWindowPosition = window.position

            window.size = Vector2(
                displays[0].width!!.toDouble() / displays[0].contentScale!!,
                displays[0].height!!.toDouble() / displays[0].contentScale!!
            )
            window.position = Vector2.ZERO
        }
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

    fun presentWithPixelPerfectScaling(drawer: Drawer, source: ColorBuffer, sourceSize: Vector2, targetSize: Vector2) {
        val scaleX = (targetSize.x / sourceSize.x).toInt()
        val scaleY = (targetSize.y / sourceSize.y).toInt()
        val scale = max(min(scaleY, scaleX), 1)

        val scaledWidth = sourceSize.x * scale
        val scaledHeight = sourceSize.y * scale

        val origin = Vector2(
            (targetSize.x - scaledWidth) / 2.0,
            (targetSize.y - scaledHeight) / 2.0,
        )

        drawer.image(source, origin, scaledWidth, scaledHeight)
    }

    program {
        val mainTarget = renderTarget(canvasWidth, canvasHeight) { colorBuffer() }
        mainTarget.colorBuffer(0).filter(MinifyingFilter.NEAREST, MagnifyingFilter.NEAREST)

        val motionBlurCategories = mapOf(
            "none" to MotionBlurTarget(0.0, renderTarget(mainTarget.width, mainTarget.height) { colorBuffer() }),
            "small" to MotionBlurTarget(0.8, renderTarget(mainTarget.width, mainTarget.height) { colorBuffer() }),
            "large" to MotionBlurTarget(0.95, renderTarget(mainTarget.width, mainTarget.height) { colorBuffer() }),
        )

        val fadeFilter = FadeFilter()
        val gradientFilter = GradientFilter()

        val backgroundTarget = renderTarget(canvasWidth, canvasHeight) {
            colorBuffer()
            depthBuffer()
        }
        generateBackgroundImage(drawer, backgroundTarget, gradientFilter)

        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                generateBackgroundImage(drawer, backgroundTarget, gradientFilter)
            }
            if (it.key == KEY_SPACEBAR) {
                fireFirework()
            }
            if (it.name == "f") {
                toggleFullscreen(window)
            }
            if (it.key == KEY_ESCAPE) {
                application.exit()
            }
        }

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

                val background = backgroundTarget.colorBuffer(0)
                drawer.image(background, Vector2.ZERO, canvasWidth.toDouble(), canvasHeight.toDouble())
                for (mbc in motionBlurCategories) {
                    drawBufferWithAlpha(drawer, mbc.value.target.colorBuffer(0))
                }
            }

            presentWithPixelPerfectScaling(
                drawer,
                mainTarget.colorBuffer(0),
                Vector2(canvasWidth.toDouble(), canvasHeight.toDouble()),
                Vector2(width.toDouble(), height.toDouble())
            )
        }
    }
}
