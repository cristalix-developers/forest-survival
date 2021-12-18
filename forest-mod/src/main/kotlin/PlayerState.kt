
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.ceil
import kotlin.math.min

object PlayerState {

    private var healthIndicator: HealthIndicator? = null
    private var energyIndicator: EnergyIndicator? = null
    private var waterIndicator: WaterIndicator? = null

    private var health = 0
    private var hunger = 0
    private var water = 0

    init {
        registerHandler<RenderTickPre> {
            val currentHealth = ceil(JavaMod.clientApi.minecraft().player.health).toInt()
            if (currentHealth != health) {

                if (currentHealth < health)
                    GlowEffect.show(0.1, 255, 0, 0, (20 - currentHealth) * 0.05)

                health = currentHealth
                healthIndicator?.updatePercentage(currentHealth, 20)
            }
        }
        display()

        App::class.mod.registerChannel("food-level") {
            val foodLevel = readInt()

            if (foodLevel != hunger) {
                hunger = foodLevel
                energyIndicator?.updatePercentage(foodLevel, 20)
            }
        }

        App::class.mod.registerChannel("water-level") {
            val waterLevel = readInt()

            if (waterLevel != water) {
                water = waterLevel
                waterIndicator?.updatePercentage(waterLevel, 20)
            }
        }
    }

    private fun display() {
        healthIndicator = HealthIndicator()
        waterIndicator = WaterIndicator()
        energyIndicator = EnergyIndicator()

        healthIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "health_bar.png")
        waterIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "energy.png")
        energyIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "xp_bar.png")

        val parent = rectangle {
            align = BOTTOM_RIGHT
            origin = BOTTOM_RIGHT
            color = TRANSPARENT
            size = V3(180.0, 150.0)
            addChild(healthIndicator!!, energyIndicator!!, waterIndicator!!)
        }

        UIEngine.overlayContext.addChild(parent)

        healthIndicator!!.updatePercentage(20, 20)
        waterIndicator!!.updatePercentage(20, 20)
        energyIndicator!!.updatePercentage(20, 20)
    }

    class HealthIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.LEFT
            align = Relative.LEFT
            offset.x = 5.0
        }

        private val icon = rectangle {
            origin = CENTER
            align = CENTER
            color = Color(122, 122, 122)
            size = V3(21.0, 21.0)
            textureLocation = ResourceLocation.of(NAMESPACE, "${NAMESPACE}/health.png")
            offset.x -= 50
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            align = Relative.BOTTOM_RIGHT
            origin = Relative.BOTTOM_RIGHT
            size = V3(80.0, 10.0)
            offset = V3(-5.0, -45.0)

            val parentSize = size
            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text, icon)
        }

        fun updatePercentage(current: Int, max: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max.toDouble())
            }
            this.text.content = "§f$current"
        }
    }

    class WaterIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.LEFT
            align = Relative.LEFT
            offset.x = 5.0
        }

        private val icon = rectangle {
            origin = CENTER
            align = CENTER
            color = Color(122, 122, 122)
            size = V3(11.0, 11.0)
            textureLocation = ResourceLocation.of(NAMESPACE, "${NAMESPACE}/water.png")
            offset.x -= 50
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(1.0, -30.0)
            align = Relative.BOTTOM_RIGHT
            origin = Relative.BOTTOM_RIGHT
            size = V3(80.0, 10.0)
            offset = V3(-5.0, -33.0)

            val parentSize = size

            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text, icon)
        }

        fun updatePercentage(water: Int, maxWater: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, water / maxWater.toDouble())
            }
            this.text.content = "§f$water"
        }
    }

    class EnergyIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.LEFT
            align = Relative.LEFT
            offset.x = 5.0
        }

        private val icon = rectangle {
            origin = CENTER
            align = CENTER
            color = Color(122, 122, 122)
            size = V3(21.0, 21.0)
            textureLocation = ResourceLocation.of(NAMESPACE, "${NAMESPACE}/food.png")
            offset.x -= 50
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(1.0, -30.0)
            align = Relative.BOTTOM_RIGHT
            origin = Relative.BOTTOM_RIGHT
            size = V3(80.0, 10.0)
            offset = V3(-5.0, -21.0)

            val parentSize = size

            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text, icon)
        }

        fun updatePercentage(energy: Int, maxEnergy: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, energy / maxEnergy.toDouble())
            }
            this.text.content = "§f$energy"
        }
    }
}