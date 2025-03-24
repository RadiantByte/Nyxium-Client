package com.radiantbyte.nyxium.client.game

object TranslationManager {

    private val map = HashMap<String, Map<String, String>>()

    init {
        map["en"] = en()
    }

    private fun en() = buildMap {
        put("zoom", "Zoom")
        put("nightVision", "Full Bright")
        put("sprint", " Auto Sprint")
        put("no_hurt_camera", "No Hurt Cam")
        put("free_camera", "Free Camera")
        put("darkness", "Darkness")
        put("time_shift", "Time Changer")
        put("weather_controller", "Weather Control")
        put("fake_death", "FakeDeath")
        put("explosion_particle", "Explosion")
        put("bubble_particle", "Bubble")
        put("heart_particle", "Heart")
        put("xp_levels", "FakeXP")
        put("dust_particle", "Dust")
        put("fizz_particle", "Fizz")
        put("ender_eye_particle", "Ender Eye")
        put("breeze_block_explosion_particle", "Breeze")
        put("speed_display", "Speed Display")
        put("no_chat", "NoChat")
        put("network_info", "NetworkInfo")
        put("coordinates", "Coordinates")
        put("world_state", "World State")

        // Below for module options
        put("amplifier", "Amplifier")
        put("repeat", "Repeat")
        put("delay", "Delay")
        put("enabled", "Enabled")
        put("disabled", "Disabled")
        put("time", "Time")
        put("keep_distance", "Distance")
        put("packets", "Packets")
        put("clear", "Clear")
        put("rain", "Rain")
        put("thunderstorm", "Thunderstorm")
        put("intensity", "Intensity")
        put("interval", "Interval")
        put("count", "Count")
        put("random_offset", "Random Offset")
        put("offset_radius", "Offset Radius")
        put("height_offset", "Height Offset")
        put("levels", "Levels")
        put("circle_radius", "Circle Radius")
        put("particles", "Particles")
        put("visualize", "Visualize")
        put("colored_text", "Colored Text")
        put("speed_smoothing", "Speed Smoothing")
        put("block_all", "Block All")
        put("block_player_chat", "Block Player Chat")
        put("block_system_chat", "Block System Chat")
        put("block_whispers", "Block Whispers");
        put("block_announcements", "Block Announcements")
        put("block_join_leave", "Block Join/Leave")
        put("height", "Height")
        put("width", "Width")
        put("decimal_places", "Decimal Places")
        put("show_direction", "Show Direction")
        put("show_packets", "Show Packets")
        put("show_entities", "Show Entities")
        put("show_players", "Show Players")
        put("show_time", "Show Time")
        put("show_chunks", "Show Chunks")
        put("colored_text", "Colored Text")
        put("update_interval", "Update Interval")

    }

    fun getTranslationMap(language: String): Map<String, String> {
        val translationMap = map[language]
        if (translationMap != null) {
            return translationMap
        }

        map.forEach { (key, value) ->
            if (key.startsWith(language)) {
                return value
            }
        }

        return map["en"]!!
    }

}
