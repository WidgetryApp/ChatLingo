package net.dobreguy.uniteslator.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "uniteslator")
@Config.Gui.Background("minecraft:textures/block/obsidian.png")
public class UniteslatorConfig implements ConfigData {
    public double chatRange = 30.0D;
    @Comment("Chat time is in ticks")
    public int chatTime = 200;
    public int maxChatWidth = 180;
    public int chatColor = 1315860;
    public float chatHeight = 0.0f;
    public float backgroundOpacity = 0.7F;
    public float backgroundRed = 1.0F;
    public float backgroundGreen = 1.0F;
    public float backgroundBlue = 1.0F;
    public boolean showOwnBubble = true;
}
