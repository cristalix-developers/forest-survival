
import dev.xdark.clientapi.event.input.KeyPress
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.ClickHandler
import ru.cristalix.uiengine.element.ContextGui
import ru.cristalix.uiengine.utility.*

class TentSettings {

    private val gui = ContextGui()

    init {
        val blackout = rectangle {
            size = V3(2000.0, 2000.0, 0.0)
            origin = CENTER
            align = CENTER
            color = Color(0, 0, 0, 0.86)
        }
        val main = rectangle {
            size = V3(300.0, 160.0, 0.0)
            origin = CENTER
            color = Color(0, 0, 0, 0.0)
            align = CENTER
            addChild(text {
                origin = TOP
                align = TOP
                color = WHITE
                scale = V3(2.0, 2.0, 2.0)
                size = V3(300.0, 30.0, 0.0)
                content = "Ваша палатка"
                shadow = true
            }, rectangle {
                origin = LEFT
                align = LEFT
                color = Color(190, 190, 190, 0.3)
                size = V3(90.0, 90.0, 0.0)
                onHover { this.color.alpha = if (hovered) 0.5 else 0.3 }
                onClick = accept("/tent hide")
                addChild(text {
                    origin = BOTTOM
                    align = BOTTOM
                    color = WHITE
                    offset.y = -10.0
                    size = V3(90.0, 30.0, 0.0)
                    content = "Убрать"
                    shadow = true
                })
            }, rectangle {
                origin = RIGHT
                align = RIGHT
                color = Color(255, 190, 190, 0.3)
                size = V3(90.0, 90.0, 0.0)
                onHover {
                    this.color.alpha = if (hovered) 0.5 else 0.3
                }
                onClick { gui.close() }
                addChild(text {
                    origin = BOTTOM
                    align = BOTTOM
                    color = WHITE
                    offset.y = -10.0
                    size = V3(90.0, 30.0, 0.0)
                    content = "Выйти"
                    shadow = true
                })
            }, rectangle {
                origin = V3(0.5, 0.5)
                align = V3(0.5, 0.5)
                color = Color(190, 255, 255, 0.3)
                size = V3(90.0, 90.0, 0.0)
                onHover { this.color.alpha = if (hovered) 0.5 else 0.3 }
                onClick = accept("/tent chest")
                addChild(text {
                    origin = BOTTOM
                    align = BOTTOM
                    color = WHITE
                    offset.y = -10.0
                    size = V3(90.0, 30.0, 0.0)
                    content = "Хранилище"
                    shadow = true
                })
            })
        }

        gui.addChild(blackout, main)

        App::class.mod.registerChannel("tent-open") {
            gui.open()
            gui.enabled = true
        }

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_ESCAPE && gui.enabled) {
                gui.close()
                gui.enabled = false
            }
        }
    }

    private fun accept(command: String): ClickHandler {
        return {
            if (gui.enabled) {
                gui.enabled = false
                clientApi.minecraft().setIngameFocus()
                if (command.isNotEmpty())
                    clientApi.chat().sendChatMessage(command)
            }
        }
    }
}