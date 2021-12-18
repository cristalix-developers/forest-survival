import dev.xdark.clientapi.event.input.KeyPress
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.ClickHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

class TentSettings {

    private val wrapper = rectangle {
        size = V3(1000.0, 1000.0, 0.0)
        color = Color(0, 0, 0, 0.62)
        origin = CENTER
        align = CENTER
        enabled = false
    }

    private var lock = false

    init {
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
                onClick = accept("")
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

        wrapper.addChild(main)

        App::class.mod.registerChannel("tent-open") {
            wrapper.enabled = true
            clientApi.minecraft().setIngameNotInFocus()
            lock = true
            UIEngine.schedule(1) {
                lock = false
            }
        }

        registerHandler<KeyPress> {
            if ((key == Keyboard.KEY_X || key == Keyboard.KEY_ESCAPE) && wrapper.enabled) {
                wrapper.enabled = false
                clientApi.minecraft().setIngameFocus()
                lock = true
                UIEngine.schedule(1) {
                    lock = false
                }
            }
        }
        UIEngine.overlayContext.addChild(wrapper)
    }

    private fun accept(command: String): ClickHandler {
        return {
            if (wrapper.enabled && !lock) {
                wrapper.enabled = false
                clientApi.minecraft().setIngameFocus()
                if (command.isNotEmpty())
                    clientApi.chat().sendChatMessage(command)
                lock = true
                UIEngine.schedule(1) {
                    lock = false
                }
            }
        }
    }
}