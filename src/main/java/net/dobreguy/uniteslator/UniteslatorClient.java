package net.dobreguy.uniteslator;

import me.shedaniel.autoconfig.AutoConfig;
import net.dobreguy.uniteslator.config.UniteslatorConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class UniteslatorClient implements ClientModInitializer {
    public static UniteslatorConfig CONFIG = new UniteslatorConfig();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(UniteslatorConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(UniteslatorConfig.class).getConfig();
    }
}
