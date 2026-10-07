package com.suting.mecanumwheels;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MecanumConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue ROLL;
    public static final ModConfigSpec.DoubleValue FAM;
    public static final ModConfigSpec.DoubleValue SIDE;

    static {
        BUILDER.push("wheel");
        BUILDER.comment("Mecanum wheel configuration");

        ROLL = BUILDER
                .comment("Rolling resistance coefficient")
                .defineInRange("roll", 0.1, 0.0, 100.0);

        FAM = BUILDER
                .comment("Mecanum forward thrust coefficient (along rolling direction)")
                .defineInRange("fam", 0.1, 0.0, 10.0);

        SIDE = BUILDER
                .comment("Mecanum side thrust coefficient (along wheel axis)")
                .defineInRange("side", 0.1, 0.0, 10.0);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}