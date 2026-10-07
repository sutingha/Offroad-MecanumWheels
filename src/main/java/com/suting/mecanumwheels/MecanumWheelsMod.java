package com.suting.mecanumwheels;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(MecanumWheelsMod.MODID)
public class MecanumWheelsMod {
    public static final String MODID = "mecanumwheels";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MecanumWheelsMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, MecanumConfig.SPEC);
        WheelsForceGroups.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        MecanumWheelsCommands.register(event.getDispatcher());
    }
}