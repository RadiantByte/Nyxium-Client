package com.radiantbyte.nyxium.client.game


import com.radiantbyte.nyxium.client.application.AppContext
import com.radiantbyte.nyxium.client.game.module.implementation.BlindnessModule
import com.radiantbyte.nyxium.client.game.module.implementation.DarknessModule
import com.radiantbyte.nyxium.client.game.module.implementation.NightVisionModule
import com.radiantbyte.nyxium.client.game.module.implementation.CommandHandlerModule
import com.radiantbyte.nyxium.client.game.module.implementation.FakeDeathModule
import com.radiantbyte.nyxium.client.game.module.implementation.FakeXPModule
import com.radiantbyte.nyxium.client.game.module.implementation.NoChatModule
import com.radiantbyte.nyxium.client.game.module.implementation.TimeShiftModule
import com.radiantbyte.nyxium.client.game.module.implementation.WeatherControllerModule
import com.radiantbyte.nyxium.client.game.module.implementation.SprintModule
import com.radiantbyte.nyxium.client.game.module.implementation.BreezeWindExplosionParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.BubbleParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.DustParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.ExplosionParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.EyeOfEnderDeathParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.FizzParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.HeartParticleModule
import com.radiantbyte.nyxium.client.game.module.implementation.NetworkInfoModule
import com.radiantbyte.nyxium.client.game.module.implementation.NoHurtCameraModule
import com.radiantbyte.nyxium.client.game.module.implementation.PositionDisplayModule
import com.radiantbyte.nyxium.client.game.module.implementation.SpeedDisplayModule
import com.radiantbyte.nyxium.client.game.module.implementation.WorldStateModule
import com.radiantbyte.nyxium.client.game.module.implementation.ZoomModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

object ModuleManager {

    private val _modules: MutableList<Module> = ArrayList()

    val modules: List<Module> = _modules

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        with(_modules) {
            add(ZoomModule())
            add(NightVisionModule())
            add(SprintModule())
            add(NoHurtCameraModule())
            add(BlindnessModule())
            add(DarknessModule())
            add(TimeShiftModule())
            add(WeatherControllerModule())
            add(FakeDeathModule())
            add(ExplosionParticleModule())
            add(BubbleParticleModule())
            add(HeartParticleModule())
            add(FakeXPModule())
            add(DustParticleModule())
            add(EyeOfEnderDeathParticleModule())
            add(FizzParticleModule())
            add(BreezeWindExplosionParticleModule())
            add(NoChatModule())
            add(SpeedDisplayModule())
            add(PositionDisplayModule())
            add(CommandHandlerModule())
            add(NetworkInfoModule())
            add(WorldStateModule())
        }
    }

    fun saveConfig() {
        val configsDir = AppContext.instance.filesDir.resolve("configs")
        configsDir.mkdirs()

        val config = configsDir.resolve("UserConfig.json")
        val jsonObject = buildJsonObject {
            put("modules", buildJsonObject {
                _modules.forEach {
                    if (it.private) {
                        return@forEach
                    }
                    put(it.name, it.toJson())
                }
            })
        }

        config.writeText(json.encodeToString(jsonObject))
    }

    fun loadConfig() {
        val configsDir = AppContext.instance.filesDir.resolve("configs")
        configsDir.mkdirs()

        val config = configsDir.resolve("UserConfig.json")
        if (!config.exists()) {
            return
        }

        val jsonString = config.readText()
        if (jsonString.isEmpty()) {
            return
        }

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val modules = jsonObject["modules"]!!.jsonObject
        _modules.forEach { module ->
            (modules[module.name] as? JsonObject)?.let {
                module.fromJson(it)
            }
        }
    }

}