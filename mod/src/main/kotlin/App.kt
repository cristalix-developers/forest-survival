
import dev.xdark.clientapi.event.input.KeyPress
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val NAMESPACE = "cache/animation"

lateinit var mod: App

class App : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        mod = this

        Guide()
        BonfireIndicator()
        Temperature()
        TentSettings()
        CorpseManager()

        BarManager
        Highlight()
        Banner()

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_H)
                clientApi.chat().sendChatMessage("/craft")
        }

    }
}