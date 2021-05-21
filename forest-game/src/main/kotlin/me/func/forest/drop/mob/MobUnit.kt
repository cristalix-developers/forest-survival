package me.func.forest.drop.mob

import me.func.forest.app
import me.func.forest.drop.dropper.Dropper
import me.func.forest.drop.dropper.RandomItemDrop
import me.func.forest.item.ItemList
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

enum class MobUnit(
    private val title: String,
    val exp: Int,
    val respawnTime: Int,
    private val health: Double,
    private val type: EntityType,
    private val dropper: Dropper,
) {

    RABBIT("⭐ §7Кролик", 2, 30, 5.0, EntityType.RABBIT, RandomItemDrop(2, ItemList.RABBIT_MEAL1)),
    WOLF("⭐⭐ §aВолк", 5, 300, 50.0, EntityType.WOLF, RandomItemDrop(2, ItemList.WOLF_MEAL1)),
    BEAR("⭐⭐⭐ §cМедведь", 20, 500, 120.0, EntityType.POLAR_BEAR, RandomItemDrop(1, ItemList.BEAR_MEAL1)),;

    fun spawn(location: Location) {
        val mob = location.world.spawnEntity(location, type) as LivingEntity

        mob.isCustomNameVisible = true
        mob.customName = title
        mob.maxHealth = health
        mob.health = health

        if (this == WOLF) {
            (mob as Wolf).isAngry = true
        }
        if (this == BEAR)
            mob.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 100000, 2))

        mob.setMetadata("unit", FixedMetadataValue(app, this.name))
    }

    fun drop(location: Location, player: Player) {
        dropper.drop(ItemList.LEATHER1, location, player)
    }

}