package com.radiantbyte.nyxium.client.game.module.implementation

import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket

class NetworkInfoModule : Module("network_info", ModuleCategory.Implementation) {

    private var lastDisplayTime = 0L
    private val displayInterval = 500L
    private val colorStyle by boolValue("colored_text", true)
    private val showPacketCounts by boolValue("show_packets", true)

    private var incomingPackets = 0
    private var outgoingPackets = 0
    private var lastPacketCountReset = 0L
    private val packetCountInterval = 1000L

    private var lastPingSentTime = 0L
    private var currentPing = 0L

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        incomingPackets++

        if (packet is PlayerAuthInputPacket) {
            if (lastPingSentTime > 0) {
                currentPing = System.currentTimeMillis() - lastPingSentTime
            }

            val currentTime = System.currentTimeMillis()

            if (currentTime - lastPacketCountReset >= packetCountInterval) {
                lastPacketCountReset = currentTime
                incomingPackets = 0
                outgoingPackets = 0
            }

            if (currentTime - lastDisplayTime >= displayInterval) {
                lastDisplayTime = currentTime

                val networkText = if (colorStyle) {
                    buildString {
                        append("§l§b[Network] §r")
                        append("§fPing: §a${currentPing}ms")
                        if (showPacketCounts) {
                            append(" §f| §fPackets: §a↑$outgoingPackets §c↓$incomingPackets")
                        }
                    }
                } else {
                    buildString {
                        append("Network: ")
                        append("Ping: ${currentPing}ms")
                        if (showPacketCounts) {
                            append(" | Packets: ↑$outgoingPackets ↓$incomingPackets")
                        }
                    }
                }

                session.clientBound(SetTitlePacket().apply {
                    type = SetTitlePacket.Type.ACTIONBAR
                    text = networkText
                    fadeInTime = 0
                    fadeOutTime = 0
                    stayTime = 2
                    xuid = ""
                    platformOnlineId = ""
                })
            }
        }
    }

    override fun afterPacketBound(packet: BedrockPacket) {
        if (!isEnabled) return

        outgoingPackets++
        lastPingSentTime = System.currentTimeMillis()
    }
}