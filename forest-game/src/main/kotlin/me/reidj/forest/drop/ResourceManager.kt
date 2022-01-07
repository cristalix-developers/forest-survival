package me.reidj.forest.drop

import clepto.bukkit.B
import implario.ListUtils
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.drop.booty.BonfireBooty
import me.reidj.forest.drop.dropper.DropItem
import me.reidj.forest.drop.generator.BonfireGenerator
import me.reidj.forest.drop.mob.MobUnit
import me.reidj.forest.item.ItemList
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerFishEvent

class ResourceManager : Listener {

    private var blockUnit: Map<Location, BlockUnit> = app.worldMeta.getLabels("drop")
        .associate { it.toBlockLocation() to BlockUnit.valueOf(it.tag.toUpperCase()) }

    private var mobUnit: Map<Location, MobUnit> = app.worldMeta.getLabels("mob")
        .associate { it.toBlockLocation() to MobUnit.valueOf(it.tag.toUpperCase()) }

    private var fishLoot = arrayOf(
        ItemList.STRING1, ItemList.STICK1, ItemList.ARROW1, ItemList.SHELL2, ItemList.POISONED_ARROW1
    )

    init {
        blockUnit.forEach { (location, resource) -> resource.generate(location) }
        mobUnit.forEach { (location, resource) -> resource.spawn(location) }
    }

    @EventHandler
    fun BlockBreakEvent.handle() {
        val resource = blockUnit[block.location]
        val item = resource?.generator!!.getStand().item
        if (block.typeId == item.typeId && block.data == item.data.data)
            resource.booty(block.location, player)

        val bonfire = BonfireGenerator.BONFIRES[block.location]
        if (bonfire != null)
            BonfireBooty.get(BlockUnit.FIRE, block.location, app.getUser(player)!!)

        cancel = true
    }

    @EventHandler
    fun ProjectileHitEvent.handle() {
        if (hitBlock != null && entityType == EntityType.ARROW) {
            entity.remove()
            DropItem.drop(ItemList.STICK1, entity.location, null)
        } else if (entityType == EntityType.TIPPED_ARROW)
            entity.remove()
    }

    @EventHandler
    fun PlayerFishEvent.handle() {
        if (state == PlayerFishEvent.State.CAUGHT_FISH) {
            (caught as Item).itemStack = ListUtils.random(fishLoot).item
            app.getUser(player)!!.giveExperience(5)
        }
    }

    @EventHandler
    fun EntityCombustEvent.handle() {
        if (entityType == EntityType.STRAY)
            cancel = true
    }

    @EventHandler
    fun EntityDeathEvent.handle() {
        if (entity.hasMetadata("unit")) {
            val mob = MobUnit.valueOf(entity.getMetadata("unit")[0].asString())
            val killer = (entity as LivingEntity).killer
            var player: CraftPlayer? = null

            if (killer is CraftPlayer)
                player = killer
            else if (killer is Projectile && killer.shooter is CraftPlayer)
                player = killer.shooter as CraftPlayer

            drops = listOf()

            B.postpone(20 * mob.respawnTime) { mob.spawn(ListUtils.random(mobUnit.keys.toList())) }

            if (player != null) {
                mob.drop(entity.location, player)

                val user = app.getUser(player)!!

                user.giveExperience(mob.exp)
                user.stat.killMobs++
                ModHelper.highlight(user, "§f§l+${mob.exp} §bexp")
            }
        }
    }
}