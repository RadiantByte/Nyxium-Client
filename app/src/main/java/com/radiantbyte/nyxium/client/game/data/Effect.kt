package com.radiantbyte.nyxium.client.game.data

class Effect(val id: Int, var amplifier: Int, var duration: Int) {

    companion object {
        const val BLINDNESS = 15
        const val NIGHT_VISION = 16
        const val DARKNESS = 30
    }
}