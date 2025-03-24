package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import com.radiantbyte.nyxium.client.game.ModuleManager
import org.cloudburstmc.protocol.bedrock.packet.TextPacket

class CommandHandlerModule : Module("command_handler", ModuleCategory.Implementation, true, true) {
    private val prefix = "."

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is TextPacket && packet.type == TextPacket.Type.CHAT) {
            val message = packet.message
            if (!message.startsWith(prefix)) return

            interceptablePacket.intercept()

            val args = message.substring(prefix.length).split(" ")
            val command = args[0].lowercase()

            when (command) {
                "help" -> {
                    displayHelp(args.getOrNull(1))
                }
                else -> {
                    val module = ModuleManager.modules.find { it.name.equals(command, ignoreCase = true) }
                    if (module != null && !module.private) {
                        module.isEnabled = !module.isEnabled
                    } else {
                        session.displayClientMessage("§l§b[NyxiumClient] §r§cModule not found: §f$command")
                    }
                }
            }
        }
    }

    private fun displayHelp(category: String?) {
        val header = """
            §l§b[NyxiumClient] §r§7Module List
            §7Commands:
            §f.help <category> §7- View modules in a category
            §f.<module> §7- Toggle a module
            §f.help §7- Show all categories
            §r§7
        """.trimIndent()

        session.displayClientMessage(header)

        if (category != null) {
            try {
                val moduleCategory = ModuleCategory.valueOf(category.uppercase())
                displayCategoryModules(moduleCategory)
            } catch (e: IllegalArgumentException) {
                session.displayClientMessage("§cInvalid category: $category")
                session.displayClientMessage("§7Available categories: ${ModuleCategory.entries.joinToString("§f, §7") { it.name.lowercase() }}")
            }
            return
        }

        ModuleCategory.entries.forEach { cat ->
            displayCategoryModules(cat)
        }
    }

    private fun displayCategoryModules(category: ModuleCategory) {
        val modules = ModuleManager.modules
            .filterNot { it.private }
            .filter { it.category == category }

        if (modules.isEmpty()) return

        session.displayClientMessage("§l§b§m--------------------")
        session.displayClientMessage("§l§b${category.name} Modules:")
        session.displayClientMessage("§r§7")

        modules.chunked(3).forEach { row ->
            val formattedRow = row.joinToString("   ") { module ->
                val status = if (module.isEnabled) "§a✔️" else "§c✘"
                "$status §f${module.name}"
            }
            session.displayClientMessage(formattedRow)
        }

        session.displayClientMessage("§r§7")
    }
}