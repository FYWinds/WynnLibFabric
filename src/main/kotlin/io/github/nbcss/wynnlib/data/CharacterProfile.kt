package io.github.nbcss.wynnlib.data

import net.minecraft.client.MinecraftClient

class CharacterProfile {
    companion object {
        private val client = MinecraftClient.getInstance()
        private var profile: CharacterProfile? = CharacterProfile()
        fun getCurrentProfile(): CharacterProfile? = profile
    }

    fun getLevel(): Int {
        return client.player?.experienceLevel ?: 0
    }
}