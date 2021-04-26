package me.func.forest.user.listener.prepare

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModTransfer
import me.func.forest.user.User
import org.bukkit.Location

class TutorialLoader : PrepareUser {

    private val startLocation: Location = app.worldMeta
        .getLabel("guide_start")
        .toCenterLocation()

    override fun execute(user: User) {
        B.postpone(10) { ModTransfer.string("1").send("guide", user) }
        if (!user.watchTutorial()) {
            // test
            val player = user.player
            player.allowFlight = true
            player.isFlying = true
            player.isOp = true

            // Полет вперед
            //Cycle.run(updateTimeFlight, normalFlightTime * 20 * updateTimeFlight) {
            //    val vec = helicopter.velocity
            //
            //    vec.x = 0.0
            //    vec.y = 0.17
            //    vec.z = 0.23
            //
            //    helicopter.velocity = vec
            //}
            //
            //val omega = 2.1
            //
            //// Вращение
            //B.postpone(normalFlightTime * 20 * updateTimeFlight) {
            //    Cycle.run(updateTimeFlight, landingTime * 20 * updateTimeFlight) {
            //        val x = (kotlin.math.sin(it.toDouble() + .4) - kotlin.math.sin(it.toDouble())) * omega
            //        val z = (kotlin.math.cos((it.toDouble() + .4)) - kotlin.math.cos(it.toDouble())) * omega
            //
            //        val rot = helicopter.headPose
            //
            //        rot.x = x
            //        rot.z = z
            //
            //        helicopter.headPose = rot
            //
            //        val vec = helicopter.velocity
            //
            //        vec.x = x
            //        vec.y = 0.14
            //        vec.z = z
            //
            //        helicopter.velocity = vec
            //    }
            //}
        }
    }
}