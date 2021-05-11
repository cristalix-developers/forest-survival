package me.func.forest.drop.generator

import me.func.forest.drop.Resources
import org.bukkit.Location

@FunctionalInterface
interface Generator<T> {

    fun generate(resource: Resources, location: Location)

    fun getStand(): T

}