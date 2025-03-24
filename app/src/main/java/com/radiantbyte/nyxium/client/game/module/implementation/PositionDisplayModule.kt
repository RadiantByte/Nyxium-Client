package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket

class PositionDisplayModule : Module("coordinates", ModuleCategory.Implementation) {

    private var lastDisplayTime = 0L
    private val displayInterval = 500L
    private val colorStyle by boolValue("colored_text", true)
    private val showDirection by boolValue("show_direction", true)
    private val roundDecimals by intValue("decimal_places", 1, 0..3)

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastDisplayTime >= displayInterval) {
                lastDisplayTime = currentTime

                val pos: Vector3f = packet.position
                val yaw: Float = packet.rotation.y

                val direction = when {
                    yaw > -45 && yaw <= 45 -> "S"
                    yaw > 45 && yaw <= 135 -> "W"
                    yaw > 135 || yaw <= -135 -> "N"
                    else -> "E"
                }

                val format = "%.${roundDecimals}f"
                val posText = if (colorStyle) {
                    buildString {
                        append("§l§b[Position] §r")
                        append("§fX: ${String.format(format, pos.x)} ")
                        append("§fY: ${String.format(format, pos.y)} ")
                        append("§fZ: ${String.format(format, pos.z)}")
                        if (showDirection) {
                            append(" §7($direction)")
                        }
                    }
                } else {
                    buildString {
                        append("Position: ")
                        append("X: ${String.format(format, pos.x)} ")
                        append("Y: ${String.format(format, pos.y)} ")
                        append("Z: ${String.format(format, pos.z)}")
                        if (showDirection) {
                            append(" ($direction)")
                        }
                    }
                }

                session.clientBound(SetTitlePacket().apply {
                    type = SetTitlePacket.Type.ACTIONBAR
                    text = posText
                    fadeInTime = 0
                    fadeOutTime = 0
                    stayTime = 2
                    xuid = ""
                    platformOnlineId = ""
                })
            }
        }
    }
}