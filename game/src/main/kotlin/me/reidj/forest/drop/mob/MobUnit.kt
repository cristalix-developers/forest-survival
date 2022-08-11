package me.reidj.forest.drop.mob

import me.reidj.forest.app
import me.reidj.forest.channel.item.ItemList
import me.reidj.forest.drop.dropper.Dropper
import me.reidj.forest.drop.dropper.RandomItemDrop
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
    val respawnTime: Int,
    private val health: Double,
    private val type: EntityType,
    private val dropper: Dropper,
) {

    RABBIT("⭐ §7Кролик",  30, 5.0, EntityType.RABBIT, RandomItemDrop(2, ItemList.RABBIT_MEAL1)),
    WOLF("⭐⭐ §aВолк", 300, 50.0, EntityType.WOLF, RandomItemDrop(2, ItemList.WOLF_MEAL1)),
    ABO("⭐⭐ §aАбориген",  60, 50.0, EntityType.STRAY, RandomItemDrop(2, ItemList.ARROW1)),
    SPIDER("⭐⭐ §aПавук", 200, 120.0, EntityType.SPIDER, RandomItemDrop(1, ItemList.STRING1)),
    BEAR("⭐⭐⭐ §cМедведь",  600, 120.0, EntityType.POLAR_BEAR, RandomItemDrop(1, ItemList.BEAR_MEAL1)),;

    fun spawn(location: Location) {
        val mob = location.world.spawnEntity(location, type) as LivingEntity

        mob.isCustomNameVisible = true
        mob.customName = title
        mob.maxHealth = health
        mob.health = health

        if (this == WOLF)
            (mob as Wolf).isAngry = true
        if (this == BEAR)
            mob.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 100000, 2))

        mob.setMetadata("unit", FixedMetadataValue(app, this.name))
    }

    fun drop(location: Location, player: Player) {
        dropper.drop(ItemList.LEATHER1, location, player)
    }

}