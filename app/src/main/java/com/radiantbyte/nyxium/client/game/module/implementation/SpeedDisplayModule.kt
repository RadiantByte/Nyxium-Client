package com.radiantbyte.nyxium.client.game.module.implementation

import android.annotation.SuppressLint
import com.radiantbyte.nyxium.client.game.InterceptablePacket
import com.radiantbyte.nyxium.client.game.Module
import com.radiantbyte.nyxium.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket
import kotlin.math.sqrt

class SpeedDisplayModule : Module("speed_display", ModuleCategory.Implementation) {

    private var lastDisplayTime = 0L
    private val displayInterval = 500L
    private val colorStyle by boolValue("colored_text", true)


    private val speedHistory = ArrayDeque<Double>(5)
    private val smoothingEnabled by boolValue("speed_smoothing", true)

    @SuppressLint("DefaultLocale")
    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastDisplayTime >= displayInterval) {
                lastDisplayTime = currentTime

                val xDist = session.localPlayer.motionX
                val zDist = session.localPlayer.motionZ
                val currentSpeed = sqrt(xDist * xDist + zDist * zDist) * 20

                val smoothedSpeed = if (smoothingEnabled) {
                    speedHistory.addLast(currentSpeed.toDouble())
                    if (speedHistory.size > 5) {
                        speedHistory.removeFirst()
                    }

                    val sortedSpeeds = speedHistory.sorted()
                    if (sortedSpeeds.size >= 3) {
                        sortedSpeeds.subList(1, sortedSpeeds.size - 1).average()
                    } else {
                        sortedSpeeds.average()
                    }
                } else {
                    currentSpeed
                }

                val speedText = if (colorStyle) {
                    "§l§b[Speed] §r§f${String.format("%.2f", smoothedSpeed)} §7bps"
                } else {
                    "Speed: ${String.format("%.2f", smoothedSpeed)} bps"
                }

                session.clientBound(SetTitlePacket().apply {
                    type = SetTitlePacket.Type.ACTIONBAR
                    text = speedText
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