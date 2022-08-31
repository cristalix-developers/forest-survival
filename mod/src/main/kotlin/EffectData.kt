
import dev.xdark.clientapi.event.gui.ScreenDisplay
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Mouse
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.utility.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class EffectData(
    val title: String,
    val description: String,
    val texture: String,
    var duration: Int,
    val effect: CarvedRectangle = carved {
        val index = EffectManager.container.children.size
        val margin = 35.0
        align = Relative.TOP_LEFT
        origin = Relative.TOP_LEFT
        size = V3(30.0, 40.0, 30.0)
        color = Color(0, 0, 0, 0.62)
        offset = V3(8.0 + margin * index / 2 + size.y * index / 2, 5.0)
        +rectangle {
            align = Relative.CENTER
            origin = Relative.CENTER
            size = V3(30.0, 30.0, 30.0)
            color = WHITE
            offset.y -= 5.0
            textureLocation = ResourceLocation.of(NAMESPACE, texture)
        }
        +text {
            align = CENTER
            origin = BOTTOM
            color = WHITE
            scale = V3(0.7, 0.7, 0.7)
            shadow = true
            offset.y += 18.0
            content = "Загрузка..."
        }
    }
) {
    init {
        var hoveringText: List<String>? = null

        UIEngine.postOverlayContext.afterRender {
            hoveringText?.let {
                val resolution = UIEngine.clientApi.resolution()
                val scaleFactor = resolution.scaleFactor

                val x = Mouse.getX() / scaleFactor
                val y = resolution.scaledHeight - Mouse.getY() / scaleFactor

                val screen = UIEngine.clientApi.minecraft().currentScreen()
                screen?.drawHoveringText(it, x, y)
            }
        }

        mod.registerHandler<ScreenDisplay> {
            hoveringText = null
        }

        fun acceptHover(hovered: Boolean) {
            if (hovered && description.isNotEmpty()) {
                if (hoveringText == null)
                    hoveringText = title.split("\n") + description.split("\n")
            } else {
                hoveringText = null
            }
        }

        effect.onHover {
            acceptHover(hovered)
        }
    }

    fun timeConverter(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0)
            "$hours" + "ч"
        else if (minutes > 0)
            "$minutes" + "м"
        else
            "$seconds" + "с"
    }
}