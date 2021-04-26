package ru.func.mod

import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.resource.ResourceLocation
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

    init {
        UIEngine.registerHandler(HealthRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(ExpBarRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(HungerRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(ArmorRender::class.java) { isCancelled = true }
        UIEngine.registerHandler(AirBarRender::class.java) { isCancelled = true }

        UIEngine.registerHandler(GameLoop::class.java) {
            if (airBar != null) {
                var air = api.minecraft().player.air
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

            healthIndicator?.updatePercentage()
            energyIndicator?.updatePercentage()
            lvlIndicator?.updatePercentage()
        }

        display()
    }

    private fun display() {
        lvlIndicator = LevelIndicator()
        healthIndicator = HealthIndicator()
        energyIndicator = EnergyIndicator()

        val parent = rectangle {
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            offset.y = -14.0
        }
        parent.addChild(lvlIndicator!!, healthIndicator!!, energyIndicator!!)

        UIEngine.overlayContext.addChild(parent)

        lvlIndicator!!.updatePercentage()
        healthIndicator!!.updatePercentage()
        energyIndicator!!.updatePercentage()

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

        private val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double
        private var health = 20
        private var maxHealth = 20

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
                textureLocation = ResourceLocation.of("minecraft", "textures/health_bar.png")
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage() {
            health = api.minecraft().player.health.toInt()
            maxHealth = 20

            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, health / maxHealth.toDouble())
            }

            this.text.content = "§f$health/$maxHealth ❤"
        }
    }

    class EnergyIndicator : RectangleElement() {

        private val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double
        private var energy = 100
        private var maxEnergy = 100

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
                textureLocation = ResourceLocation.of("minecraft", "textures/xp_bar.png")
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage() {
            energy = api.minecraft().player.absorptionAmount.toInt()
            maxEnergy = 20

            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, energy / maxEnergy.toDouble())
            }

            this.text.content = "§f$energy/$maxEnergy"
        }
    }

    class LevelIndicator : RectangleElement() {

        private val bar: RectangleElement
        private val text: TextElement = text {
            origin = Relative.CENTER
            align = Relative.CENTER
            offset.x = 4.0
        }

        private val maxX: Double
        private var level = 1
        private var exp = 1
        private var needExp = 1

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
                textureLocation = ResourceLocation.of("minecraft", "textures/energy.png")
            }
            this.maxX = bar.size.x

            addChild(bar, text)
        }

        fun updatePercentage() {
            level = 10
            exp = 10
            needExp = 10

            bar.animate(0.1, Easings.CUBIC_OUT) {
                bar.size.x = maxX * min(1.0, exp / needExp.toDouble())
            }

            this.text.content = "§f$level ур. ${if (needExp == 0) "Макс. уровень" else "§b$exp/$needExp"}"
        }
    }
}