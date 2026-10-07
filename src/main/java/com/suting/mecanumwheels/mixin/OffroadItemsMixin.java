package com.suting.mecanumwheels.mixin;

import com.suting.mecanumwheels.MecanumItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.content.items.tire.TireItem;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.ryanhcode.offroad.index.OffroadItems;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
//import net.minecraft.data.recipes.RecipeCategory;
//import net.minecraft.data.recipes.ShapedRecipeBuilder;
//import net.minecraft.data.recipes.ShapelessRecipeBuilder;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.suting.mecanumwheels.Utils.offroadItem;

@Mixin(OffroadItems.class)
public class OffroadItemsMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addTire(CallbackInfo ci) {
        SimulatedRegistrate REGISTRATE = Offroad.getRegistrate();

        //小型麦轮
        MecanumItems.MECANUM_WHEEL_SMALL_CW =
                REGISTRATE.item("mecanum_wheel_small_cw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.SMALL_TIRE))
                    //.recipe((c, p) -> ShapelessRecipeBuilder
                    //        .shapeless(RecipeCategory.MISC, c.get(), 1)
                    //        .requires(offroadItem("small_tire"))
                    //        .requires(Blocks.HONEY_BLOCK.asItem())
                    //        .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(AllBlocks.SHAFT.get()))
                    //        .save(p))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();
        //镜像的小型麦轮
        MecanumItems.MECANUM_WHEEL_SMALL_CCW =
                REGISTRATE.item("mecanum_wheel_small_ccw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.SMALL_TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();
        //麦轮
        MecanumItems.MECANUM_WHEEL_CW =
                REGISTRATE.item("mecanum_wheel_cw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();
        //镜像麦轮
        MecanumItems.MECANUM_WHEEL_CCW =
                REGISTRATE.item("mecanum_wheel_ccw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

        //大型麦轮
        MecanumItems.MECANUM_WHEEL_LARGE_CW =
                REGISTRATE.item("mecanum_wheel_large_cw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.LARGE_TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

        //镜像的大型麦轮
        MecanumItems.MECANUM_WHEEL_LARGE_CCW =
                REGISTRATE.item("mecanum_wheel_large_ccw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.LARGE_TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

        //巨型麦轮
        MecanumItems.MECANUM_WHEEL_MONSTROUS_CW =
                REGISTRATE.item("mecanum_wheel_monstrous_cw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.MONSTROUS_TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();

        //镜像巨型麦轮
        MecanumItems.MECANUM_WHEEL_MONSTROUS_CCW =
                REGISTRATE.item("mecanum_wheel_monstrous_ccw", TireItem::new)
                    .properties((x) -> x.component(OffroadDataComponents.TIRE, TireLike.MONSTROUS_TIRE))
                    .model(AssetLookup.itemModelWithPartials())
                    .register();
    }
}