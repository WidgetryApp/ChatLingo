package net.dobreguy.uniteslator;

import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registry;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Uniteslator implements ModInitializer {
	public static String MOD_ID = "uniteslator";
    public static final Logger LOGGER = LoggerFactory.getLogger("uniteslator");

	public static GameEvent VOICE_GAME_EVENT;
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		LOGGER.info("Hello Fabric world!");
	}
}