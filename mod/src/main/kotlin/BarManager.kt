
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.ceil
import kotlin.math.min

const val MAX = 20

class BarManager {

    private var healthIndicator: HealthIndicator? = null
    private var energyIndicator: EnergyIndicator? = null
    private var waterIndicator: WaterIndicator? = null
    private var ammoIndicator: AmmoIndicator? = null
    private var airBar: CarvedRectangle? = null

    private var health = 0
    private var food = 0
    private var water = 0

    init {
        healthIndicator = HealthIndicator()
        energyIndicator = EnergyIndicator()
        waterIndicator = WaterIndicator()
        ammoIndicator = AmmoIndicator()

        airBar = carved {
            size = V3(130.0, 60.0)
            color.alpha = 0.68
            origin = Relative.BOTTOM_RIGHT
            align = Relative.BOTTOM_RIGHT
            offset.y -= 15.0
            addChild(healthIndicator!!, energyIndicator!!, waterIndicator!!, ammoIndicator!!)
        }

        waterIndicator!!.updateWater(20, MAX)
        ammoIndicator!!.updateAmmo(29, 30)
        ammoIndicator!!.updateAmmoInfo("5.56", 135)

        UIEngine.overlayContext.addChild(airBar!!)

        mod.registerHandler<RenderTickPre> {
            val player = UIEngine.clientApi.minecraft().player

            val currentHealth = ceil(player.health).toInt()
            if (currentHealth != health) {
                health = currentHealth
                healthIndicator?.updateHealth(health, MAX)
            }

            val currentFood = player.foodStats.foodLevel
            if (currentFood != food) {
                food = currentFood
                energyIndicator!!.updateEnergy(food, MAX)
            }
        }

        mod.registerChannel("forest:water-level") {
            val currentWater = readInt()
            if (currentWater != water) {
                water = currentWater
                waterIndicator?.updateWater(water, MAX)
            }
        }
    }

    class HealthIndicator : CarvedRectangle() {

        private val bar: CarvedRectangle

        private val text = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            scale = V3(0.9, 0.9, 0.9)
            offset.x = 54.0
        }

        private val icon = rectangle {
            textureLocation = ResourceLocation.of(NAMESPACE,"health.png")
            origin = Relative.CENTER
            align = Relative.CENTER
            size = V3(9.0, 9.0, 9.0)
            offset.x -= 49.0
            color = WHITE
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(35.0, -20.0)
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(80.0, 7.0)

            val parentSize = size
            bar = carved {
                color = Color(160, 47, 37)
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(icon, bar, text)
        }

        fun updateHealth(current: Int, max: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max.toDouble())
            }
            this.text.content = "${current * 100 / max}%"
        }
    }

    class EnergyIndicator : CarvedRectangle() {

        private val bar: CarvedRectangle

        private val text = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            scale = V3(0.9, 0.9, 0.9)
            offset.x = 54.0
        }

        private val icon = rectangle {
            textureLocation = ResourceLocation.of(NAMESPACE,"energy.png")
            origin = Relative.CENTER
            align = Relative.CENTER
            size = V3(9.0, 9.0, 9.0)
            offset.x -= 49.0
            color = WHITE
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(35.0, -8.0)
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(80.0, 7.0)

            val parentSize = size

            bar = carved {
                color = Color(94, 85, 65)
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(icon, bar, text)
        }

        fun updateEnergy(current: Int, max: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max.toDouble())
            }
            this.text.content = "${current * 100 / max}%"
        }
    }

    class AmmoIndicator: RectangleElement() {

        private val icon = rectangle {
            textureLocation = ResourceLocation.of(NAMESPACE,"magazine.png")
            origin = Relative.CENTER
            align = Relative.CENTER
            size = V3(13.0, 13.0, 13.0)
            color = WHITE
        }

        private val ammo = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(1.5, 1.5, 1.5)
            offset = V3(29.0, 0.5)
        }

        private val ammoName = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(1.0, 1.0, 1.0)
            offset = V3(-27.0, -5.0)
        }

        private val ammoValues = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(1.0, 1.0, 1.0)
            offset = V3(-34.0, 4.5)
        }

        init {
            color = TRANSPARENT
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(50.0, 30.0)
            offset = V3(24.9, 19.0)

            addChild(icon, ammo, ammoName, ammoValues)
        }

        fun updateAmmo(ammo: Int, maxAmmo: Int) {
            this.ammo.content = "$ammo/$maxAmmo"
        }

        fun updateAmmoInfo(name: String, values: Int) {
            ammoName.content = "$name C"
            ammoValues.content = "$values"
        }
    }

    class WaterIndicator : CarvedRectangle() {

        private val bar: CarvedRectangle

        private val text = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            scale = V3(0.9, 0.9, 0.9)
            offset.x = 54.0
        }

        private val icon = rectangle {
            textureLocation = ResourceLocation.of(NAMESPACE,"water.png")
            origin = Relative.CENTER
            align = Relative.CENTER
            size = V3(9.0, 9.0, 9.0)
            offset.x -= 49.0
            color = WHITE
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(35.0, 4.0)
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(80.0, 7.0)

            val parentSize = size

            bar = carved {
                color = Color(46, 201, 186)
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(icon, bar, text)
        }

        fun updateWater(current: Int, max: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max.toDouble())
            }
            this.text.content = "${current * 100 / max}%"
        }
    }
}