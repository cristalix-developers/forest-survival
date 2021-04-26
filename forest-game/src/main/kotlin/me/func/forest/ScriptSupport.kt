package me.func.forest

import clepto.bukkit.command.CommandManager
import clepto.bukkit.command.Commands
import clepto.bukkit.event.EventContext
import clepto.bukkit.event.EventContextProxy
import groovy.lang.Script

abstract class ScriptSupport : Script(), EventContextProxy, CommandManager.Proxy {
    private val manager = Commands.getManager()
    private val context = EventContext { true }

    override fun getEventContext(): EventContext {
        return context
    }

    override fun getCommandManager(): CommandManager {
        return manager
    }
}