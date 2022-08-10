
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.ceil
import kotlin.math.min

object BarManager {

    private var healthIndicator: HealthIndicator? = null
    private var energyIndicator: EnergyIndicator? = null
    private var ammoIndicator: AmmoIndicator? = null
    private var airBar: CarvedRectangle? = null

    private var health = 0
    private var hunger = 0

    init {
        healthIndicator = HealthIndicator()
        energyIndicator = EnergyIndicator()
        ammoIndicator = AmmoIndicator()

        airBar = carved {
            size = V3(130.0, 60.0)
            color.alpha = 0.68
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            offset = V3(413.0, -15.0)
            addChild(healthIndicator!!, energyIndicator!!, ammoIndicator!!)
        }

        energyIndicator!!.updateEnergy(1, 1)
        ammoIndicator!!.updateAmmo(29, 30)
        ammoIndicator!!.updateAmmoInfo("5.56", 135)

        UIEngine.overlayContext.addChild(airBar!!)

        mod.registerHandler<RenderTickPre> {
            val currentHealth = ceil(UIEngine.clientApi.minecraft().player.health).toInt()
            if (currentHealth != health) {
                health = currentHealth
                healthIndicator?.updateHealth(health, 20)
            }
        }

        /*display()

        mod.registerChannel("food-level") {
            val foodLevel = readInt()

            if (foodLevel != hunger) {
                hunger = foodLevel
                energyIndicator?.updatePercentage(foodLevel, 20)
            }
        }*/
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
            textureLocation = ResourceLocation.of(NAMESPACE,"cartridge.png")
            origin = Relative.CENTER
            align = Relative.CENTER
            size = V3(21.0, 21.0, 21.0)
            color = WHITE
        }

        private val ammo = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(2.0, 2.0, 2.0)
            offset = V3(21.0, 3.5)
        }

        private val maxAmmo = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(1.3, 1.3, 1.3)
            offset = V3(41.0, 5.9)
        }


        private val ammoName = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(1.2, 1.2, 1.2)
            offset = V3(-27.0, -6.5)
        }

        private val ammoValues = text {
            origin = CENTER
            align = CENTER
            shadow = true
            scale = V3(1.2, 1.2, 1.2)
            offset = V3(-34.0, 6.5)
        }

        init {
            color = TRANSPARENT
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(50.0, 30.0)
            offset = V3(24.9, 15.0)

            addChild(icon, ammo, maxAmmo, ammoName, ammoValues)
        }

        fun updateAmmo(ammo: Int, maxAmmo: Int) {
            this.ammo.content = "$ammo"
            this.maxAmmo.content = "$maxAmmo"
        }

        fun updateAmmoInfo(name: String, values: Int) {
            ammoName.content = "$name C"
            ammoValues.content = "$values"
        }
    }
}