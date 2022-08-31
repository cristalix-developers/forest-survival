
import dev.xdark.feder.NetUtil
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.utility.TRANSPARENT
import ru.cristalix.uiengine.utility.V3
import ru.cristalix.uiengine.utility.rectangle
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
object EffectManager {

    val container = rectangle {
        size = V3(1920.0, 30.0)
        color = TRANSPARENT
    }

    private val effects = mutableMapOf<UUID, EffectData>()

    init {
        UIEngine.overlayContext + container

        mod.registerChannel("forest:active-effect") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val title = NetUtil.readUtf8(this)
            val description = NetUtil.readUtf8(this)
            val texture = NetUtil.readUtf8(this)
            val duration = readInt()

            if (uuid in effects) {
                val data = effects[uuid] ?: return@registerChannel
                (data.effect.children[4] as TextElement).content = data.timeConverter(duration)
            } else {
                val data = EffectData(title, description, texture, duration)
                container + data.effect
                effects[uuid] = data
            }
        }

        mod.registerChannel("forest:effect-remove") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val activeEffect = effects[uuid] ?: return@registerChannel
            container.removeChild(activeEffect.effect)
            effects.remove(uuid)
        }
    }
}