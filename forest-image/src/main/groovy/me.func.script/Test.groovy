@groovy.transform.BaseScript(me.func.forest.ScriptSupport)
package me.func.script

import org.bukkit.event.player.PlayerJoinEvent

on PlayerJoinEvent, {
    player.sendMessage("Hi")
}