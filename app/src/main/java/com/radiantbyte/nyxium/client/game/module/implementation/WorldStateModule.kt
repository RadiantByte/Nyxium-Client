package com.radiantbyte.nyxium.client.game.module.implementation

import android.annotation.SuppressLint
import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket
import org.cloudburstmc.protocol.bedrock.packet.SetTimePacket

class WorldStateModule : Module("world_state", ModuleCategory.Implementation) {

    private val showEntities by boolValue("show_entities", true)
    private val showPlayers by boolValue("show_players", true)
    private val showTime by boolValue("show_time", true)
    private val showChunks by boolValue("show_chunks", true)
    private val coloredText by boolValue("colored_text", true)
    private val updateInterval by intValue("update_interval", 500, 100..2000)

    private var lastDisplayTime = 0L
    private var loadedChunks = 0
    private var worldTime = 0L

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        val packet = interceptablePacket.packet

        if (packet is UpdateBlockPacket) {
            loadedChunks++
        }

        if (packet is SetTimePacket) {
            worldTime = packet.time.toLong()
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastDisplayTime < updateInterval) {
            return
        }
        lastDisplayTime = currentTime

        if (isEnabled && isSessionCreated) {
            val text = buildString {
                if (showEntities) {
                    append(if (coloredText) "§aEntities: " else "Entities: ")
                    append(session.level.entityMap.size)
                }

                if (showPlayers) {
                    if (showEntities) append(if (coloredText) " §7| " else " | ")
                    append(if (coloredText) "§bPlayers: " else "Players: ")
                    append(session.level.playerMap.size)
                }

                if (showTime) {
                    if (showEntities || showPlayers) append(if (coloredText) " §7| " else " | ")
                    append(if (coloredText) "§eTime: " else "Time: ")
                    append(formatMinecraftTime(worldTime))
                }

                if (showChunks) {
                    if (showEntities || showPlayers || showTime) append(if (coloredText) " §7| " else " | ")
                    append(if (coloredText) "§dChunks: " else "Chunks: ")
                    append(loadedChunks)
                }
            }

            session.clientBound(SetTitlePacket().apply {
                type = SetTitlePacket.Type.ACTIONBAR
                this.text = text
                fadeInTime = 0
                fadeOutTime = 0
                stayTime = 2
                xuid = ""
                platformOnlineId = ""
            })
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatMinecraftTime(ticks: Long): String {
        val hours = ((ticks / 1000 + 6) % 24)
        val minutes = ((ticks % 1000) * 60 / 1000)
        return String.format("%02d:%02d", hours, minutes)
    }

    override fun onEnabled() {
        super.onEnabled()
        loadedChunks = 0
        worldTime = 0
    }

    override fun onDisabled() {
        super.onDisabled()
        if (isSessionCreated) {
            session.clientBound(SetTitlePacket().apply {
                type = SetTitlePacket.Type.ACTIONBAR
                text = ""
                fadeInTime = 0
                fadeOutTime = 0
                stayTime = 0
                xuid = ""
                platformOnlineId = ""
            })
        }
    }
}