package io.github.monun.sample.plugin

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import org.bukkit.plugin.java.JavaPlugin

class SamplePlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Hello world!")

        println(ServerPlayer::class.java)
        for (declaredField in Entity::class.java.declaredFields) {
            println(declaredField.name)
        }
    }
}