import ru.cristalix.clientapi.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*

class LoseHeart {

    init {
        val heart2Size = V3(170.0, 100.0)
        val heart3Size = V3(225.0, 100.0)
        val wrapper = rectangle {
            align = CENTER
            origin = CENTER
            size = heart3Size
            enabled = false
        }
        val scale10 = V3(10.0, 10.0, 10.0)
        val colorAlpha = Color(255, 255, 255, 0.1)
        val heartSize = V3(100.0, 100.0)
        val showSeconds = 2
        val hideSeconds = 2

        val left = text {
            align = LEFT
            origin = LEFT
            size = heartSize
            scale = scale10
            content = "§4❤"
            shadow = true
            color = colorAlpha
        }
        val center = text {
            align = CENTER
            origin = CENTER
            size = heartSize
            scale = scale10
            content = "§4❤"
            shadow = true
            color = colorAlpha
        }
        val away = text {
            align = RIGHT
            origin = RIGHT
            size = heartSize
            scale = scale10
            content = "§4❤"
            shadow = true
            color = colorAlpha
        }

        UIEngine.overlayContext.addChild(wrapper)

        App::class.mod.registerChannel("player-dead") {
            wrapper.enabled = true
            wrapper.removeChild(left, center, away)
            wrapper.addChild(left)

            val hearts = readInt()

            if (hearts == 2) {
                wrapper.size = heart2Size
            } else if (hearts == 3) {
                wrapper.size = heart3Size
                wrapper.addChild(center)
                center.animate(showSeconds) {
                    color.alpha = 1.0
                }
                UIEngine.schedule(showSeconds) {
                    center.animate(hideSeconds) {
                        color.alpha = 0.1
                    }
                }
            }

            wrapper.addChild(away)
            left.animate(showSeconds) {
                color.alpha = 1.0
            }
            away.animate(showSeconds) {
                color.alpha = 1.0
            }
            UIEngine.schedule(showSeconds) {
                left.animate(hideSeconds) {
                    color.alpha = 0.1
                }
                away.animate(hideSeconds) {
                    scale.x = 1.0
                    scale.y = 1.0
                    rotation = Rotation(8.0, 0.0, 0.0, 1.0)
                    color.alpha = 0.1
                }
            }
            UIEngine.schedule(showSeconds + hideSeconds) {
                wrapper.enabled = false
                away.animate(0) {
                    scale.x = 10.0
                    scale.y = 10.0
                    rotation = Rotation(0.0, 1.0, 1.0, 1.0)
                }
            }
        }
    }
}