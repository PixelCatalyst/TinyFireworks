package colors

import org.openrndr.color.ColorRGBa
import org.openrndr.math.clamp

class ColorMixer(private val palette: ColorPalette, private val mixDuration: Double) {
    private var elapsedSeconds = 0.0

    fun update(deltaSeconds: Double) {
        elapsedSeconds += deltaSeconds
    }

    fun get(): ColorRGBa {
        return palette.baseColor.mix(palette.mainColor, clamp(elapsedSeconds / mixDuration, 0.0, 1.0))
    }
}
