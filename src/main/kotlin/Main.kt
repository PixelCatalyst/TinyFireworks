
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import kotlin.random.Random

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    val fireworks = ArrayList<Firework>()

    program {

        keyboard.keyDown.listen {
            if (it.key == KEY_SPACEBAR) {
                fireworks.add(Firework(Random.nextDouble() * width, Random.nextDouble() * height))
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)

            for (f in fireworks) {
                f.draw(drawer)
            }
        }
    }
}
