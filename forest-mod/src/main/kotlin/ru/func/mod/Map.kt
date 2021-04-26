package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.max
import kotlin.math.min

class Map {

    var dragging: Boolean = false
    var draggingX: Double = 0.0
    var draggingY: Double = 0.0

    private val maxSize = 250.0
    private val normalSize = 150.0
    private val minSize = 100.0

    private var map: RectangleElement = rectangle {
        size = V3(normalSize, normalSize)
        onClick = onClick@{ _: AbstractElement, b: Boolean, _: MouseButton ->
            if (Mouse.isGrabbed()) return@onClick
            dragging = b
            if (b) {
                val mouse = getMouse()
                val resolution = api.resolution()
                draggingX =
                    mouse.x - this.offset.x - this.align.x * resolution.scaledWidth_double + this.origin.x * this.size.x
                draggingY =
                    mouse.y - this.offset.y - this.align.y * resolution.scaledHeight_double + this.origin.y * this.size.y
            }
        }
        color = WHITE
        textureLocation = ResourceLocation.of("minecraft", "textures/map.png")
    }

    private fun getMouse(): V2 {
        val resolution = UIEngine.clientApi.resolution()
        val factor = resolution.scaleFactor
        val mouseX = (Mouse.getX() / factor).toDouble()
        val mouseY = ((Display.getHeight() - Mouse.getY()) / factor).toDouble()
        return V2(mouseX, mouseY)
    }

    init {
        UIEngine.overlayContext.addChild(map)

        UIEngine.registerHandler(KeyPress::class.java) {
            if (key == Keyboard.KEY_M) {
                map.enabled = !map.enabled
            }
        }

        UIEngine.registerHandler(GameLoop::class.java) {
            if (map.enabled) {
                val dWheel = Mouse.getDWheel()
                if (dWheel != 0) {
                    map.animate(30) {
                        map.size.x = max(minSize, min(maxSize, map.size.x + (dWheel / 7).toDouble()))
                        map.size.y = max(minSize, min(maxSize, map.size.y + (dWheel / 7).toDouble()))
                    }
                }
                if (dragging) {
                    val resolution = api.resolution()
                    val factor = resolution.scaleFactor
                    val mouse = getMouse()

                    val screenWidth = resolution.scaledWidth_double
                    val screenHeight = resolution.scaledHeight_double
                    val px = (mouse.x - draggingX) / (screenWidth - map.size.x)
                    val py = (mouse.y - draggingY) / (screenHeight - map.size.y)
                    val alignX = when {
                        px < 0.33 -> 0.0
                        px > 0.66 -> 1.0
                        else -> 0.5
                    }
                    val alignY = when {
                        py < 0.33 -> 0.0
                        py > 0.66 -> 1.0
                        else -> 0.5
                    }

                    map.align = V3(alignX, alignY)
                    map.origin = V3(alignX, alignY)
                    map.offset.x =
                        ((mouse.x - draggingX + (map.size.x - screenWidth) * alignX)
                            .coerceIn(-alignX * screenWidth, (-alignX + 1) * screenWidth) * factor).toInt().toDouble() /
                                factor + if (alignX == 0.5) 0.5 else 0.0
                    map.offset.y =
                        ((mouse.y - draggingY + (map.size.y - screenHeight) * alignY)
                            .coerceIn(-alignY * screenHeight, (-alignY + 1) * screenHeight) * factor).toInt()
                            .toDouble() /
                                factor + if (alignY == 0.5) 0.5 else 0.0

                    if (!Mouse.isButtonDown(0)) dragging = false
                }
            }
        }
    }
}