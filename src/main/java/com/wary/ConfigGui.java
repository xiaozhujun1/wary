package com.wary;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static com.wary.DebugHudModifier.*;

public class ConfigGui {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = createConfigBuilder();
        return builder.setParentScreen(parent).build();
    }

    private static ConfigBuilder createConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(mc.currentScreen)
                .setTitle(Text.of("test"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable fake coords"), fakeCoordsEnabled)
                .setDefaultValue(false)
                .setTooltip(Text.of("idk"))
                .setSaveConsumer(newValue -> {
                    fakeCoordsEnabled = newValue;
                    config.fakecoordsenabled = newValue;
                    AutoConfig.getConfigHolder(Config.class).save();
                })
                .build());
        general.addEntry(entryBuilder.startIntField(Text.of("offsetX"), offsetX)
                .setDefaultValue(0)
                .setTooltip(Text.of("idk"))
                .setSaveConsumer(newValue -> {
                    offsetX = newValue;
                    config.offsetx = newValue;
                    AutoConfig.getConfigHolder(Config.class).save();
                })
                .build());
        general.addEntry(entryBuilder.startIntField(Text.of("offsetZ"), offsetZ)
                .setDefaultValue(0)
                .setTooltip(Text.of("idk"))
                .setSaveConsumer(newValue -> {
                    offsetZ = newValue;
                    config.offsetz = newValue;
                    AutoConfig.getConfigHolder(Config.class).save();
                })
                .build());
        general.addEntry(entryBuilder.startIntField(Text.of("Spawn Radius"), spawnRadius)
                .setDefaultValue(10000)
                .setTooltip(Text.of("idk"))
                .setSaveConsumer(newValue -> {
                    spawnRadius = newValue;
                    config.spawnradius = newValue;
                    AutoConfig.getConfigHolder(Config.class).save();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Disable near spawn"), disableInSpawn)
                .setDefaultValue(Boolean.FALSE)
                .setTooltip(Text.of("idk"))
                .setSaveConsumer(newValue -> {
                    disableInSpawn = newValue;
                    config.disableinspawn = newValue;
                    AutoConfig.getConfigHolder(Config.class).save();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Nether Portal Scanner"), portalScanner)
                .setDefaultValue(false)
                .setTooltip(Text.of("testtooltip"))
                .setSaveConsumer(newValue ->{
                    portalScanner = newValue;
                    config.portalScanner = newValue;
                    AutoConfig.getConfigHolder(Config.class).save();
                })
                .build());
        return builder;
    }

}
