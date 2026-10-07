package com.suting.mecanumwheels;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class MacenumWheelsCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("mecanum")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("config")
                .then(Commands.literal("get")
                    .then(Commands.literal("roll")
                        .executes(context -> {
                            double value = MacenumConfig.ROLL.get();
                            context.getSource().sendSuccess(
                                () -> Component.literal("Roll = " + value), false);
                            return 1;
                        }))
                    .then(Commands.literal("fam")
                        .executes(context -> {
                            double value = MacenumConfig.FAM.get();
                            context.getSource().sendSuccess(
                                () -> Component.literal("Fam = " + value), false);
                            return 1;
                        }))
                    .then(Commands.literal("side")
                        .executes(context -> {
                            double value = MacenumConfig.SIDE.get();
                            context.getSource().sendSuccess(
                                () -> Component.literal("Side = " + value), false);
                            return 1;
                        })))

                .then(Commands.literal("set")
                    .then(Commands.literal("roll")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
                            .executes(context -> {
                                double value = DoubleArgumentType.getDouble(context, "value");
                                MacenumConfig.ROLL.set(value);
                                MacenumConfig.SPEC.save();
                                context.getSource().sendSuccess(
                                    () -> Component.literal("Roll updated to " + value), true);
                                return 1;
                            })))
                    .then(Commands.literal("fam")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 10))
                            .executes(context -> {
                                double value = DoubleArgumentType.getDouble(context, "value");
                                MacenumConfig.FAM.set(value);
                                MacenumConfig.SPEC.save();
                                context.getSource().sendSuccess(
                                    () -> Component.literal("Fam updated to " + value), true);
                                return 1;
                            })))
                    .then(Commands.literal("side")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 10))
                            .executes(context -> {
                                double value = DoubleArgumentType.getDouble(context, "value");
                                MacenumConfig.SIDE.set(value);
                                MacenumConfig.SPEC.save();
                                context.getSource().sendSuccess(
                                    () -> Component.literal("Side updated to " + value), true);
                                return 1;
                            }))))));
    }
}