package net.dobreguy.uniteslator;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Uniteslator implements ModInitializer {
	public static String MOD_ID = "uniteslator";
    public static final Logger LOGGER = LoggerFactory.getLogger("uniteslator");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}