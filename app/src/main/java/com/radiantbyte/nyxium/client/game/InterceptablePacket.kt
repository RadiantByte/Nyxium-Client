package com.radiantbyte.nyxium.client.game

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket

data class InterceptablePacket(val packet: BedrockPacket) {

    var isIntercepted = false
        private set

    fun intercept() {
        isIntercepted = true
    }

}
