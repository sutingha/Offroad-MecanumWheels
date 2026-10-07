package com.suting.mecanumwheels;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WheelsForceGroups {
    public static final DeferredRegister<ForceGroup> FORCE_GROUPS =
            DeferredRegister.create(ForceGroups.REGISTRY_KEY, MacenumWheelsMod.MODID);

    public static final DeferredHolder<ForceGroup, ForceGroup> MECANUM_FORCES;
    
    static {
        MECANUM_FORCES = FORCE_GROUPS.register("mecanum_forces", () -> new ForceGroup(
                Component.literal("Mecanum Forces"),
                Component.literal("Mecanum wheel thrust"),
                0x4A90E2,
                true
        ));
    }

    public static void register(IEventBus modEventBus) {
        FORCE_GROUPS.register(modEventBus);
    }
}