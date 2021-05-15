package ru.func.mod

import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.resource.ResourceLocation
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Daniil Sudomoin on 18.04.2021.
 **/
class BarManager {

    private var healthIndicator: HealthIndicator? = null
    private var energyIndicator: EnergyIndicator? = null
    private var lvlIndicator: LevelIndicator? = null
    private var airBar: RectangleElement? = null

    private var airHide: Boolean = false

    private var health = 0
    private var hunger = 0
    private var exp = 0
    private var level = 0

    init {
        UIEngine.registerHandler(HealthRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(ExpBarRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(HungerRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(ArmorRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(AirBarRender::class.java) { isCancelled = true }

        UIEngine.registerHandler(RenderTickPre::class.java) {
            if (airBar != null) {
                var air = clientApi.minecraft().player.air
                air = max(0, air)

                if (!airHide)
                    airBar!!.children[0].size.x = air.toDouble() / 300 * 260.0

                if (air == 300 && !airHide) {
                    airHide = true
                    UIEngine.overlayContext.removeChild(airBar!!)
                }

                if (air < 300 && airHide) {
                    airHide = false
                    UIEngine.overlayContext.addChild(airBar!!)
                }
            }

            val currentHealth = clientApi.minecraft().player.health.toInt()
            if (currentHealth != health) {

                if (currentHealth < health)
                    GlowEffect.show(0.1, 255, 0, 0, (20 - currentHealth) * 0.05)

                health = currentHealth
                healthIndicator?.updatePercentage(currentHealth, 20)
            }
        }

        display()

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "food-level") {
                val foodLevel = data.readInt()

                if (foodLevel != hunger) {
                    hunger = foodLevel
                    energyIndicator?.updatePercentage(foodLevel, 20)
                }
            } else if (channel == "exp-level") {
                val actualLevel = data.readInt()
                val haveExp = data.readInt()
                val needExp = data.readInt()

                if (actualLevel != level || haveExp != exp) {
                    exp = haveExp
                    level = actualLevel
                    lvlIndicator?.updatePercentage(level, exp, needExp)
                }
            }
        }
    }

    private fun display() {
        lvlIndicator = LevelIndicator()
        healthIndicator = HealthIndicator()
        energyIndicator = EnergyIndicator()

        healthIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "health_bar.png")
        energyIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "xp_bar.png")
        lvlIndicator!!.bar.textureLocation = ResourceLocation.of(NAMESPACE, "energy.png")

        val parent = rectangle {
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            offset.y = -14.0
        }
        parent.addChild(lvlIndicator!!, healthIndicator!!, energyIndicator!!)

        UIEngine.overlayContext.addChild(parent)

        lvlIndicator!!.updatePercentage(1, 0, 0)
        healthIndicator!!.updatePercentage(20, 20)
        energyIndicator!!.updatePercentage(20, 20)

        airBar = rectangle {
            size = V3(260.0, 3.0)
            color.alpha = 0.68
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            offset.y = -51.0
            addChild(rectangle {
                size = V3(260.0, 3.0)
                color = WHITE
            })
        }
        UIEngine.overlayContext.addChild(airBar!!)
    }


    class HealthIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(-1.0, -30.0)
            align = Relative.CENTER
            origin = Relative.RIGHT
            size = V3(99.0, 10.0)

            val parentSize = size
            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage(current: Int, max: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, current / max.toDouble())
            }
            this.text.content = "§f$current/$max ❤"
        }
    }

    class EnergyIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset = V3(1.0, -30.0)
            align = Relative.CENTER
            origin = Relative.LEFT
            size = V3(99.0, 10.0)

            val parentSize = size

            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage(energy: Int, maxEnergy: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, energy / maxEnergy.toDouble())
            }
            this.text.content = "§f$energy/$maxEnergy"
        }
    }

    class LevelIndicator : RectangleElement() {

        val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double

        init {
            color = Color(0, 0, 0, 0.68)
            offset.y = -18.0
            align = Relative.CENTER
            origin = Relative.CENTER
            size = V3(200.0, 10.0)

            val parentSize = size
            bar = rectangle {
                color = WHITE
                size = parentSize
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage(level: Int, exp: Int, needExp: Int) {
            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, exp / needExp.toDouble())
            }
            this.text.content = "§f$level ур. ${if (needExp == 0) "Макс. уровень" else "§b$exp/$needExp"}"
        }
    }
}