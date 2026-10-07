//仅调试用的mxixin。。 
package com.suting.mecanumwheels.mixin;

import dev.ryanhcode.offroad.Offroad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Offroad.class)
public class OffroadMixin {
    @Inject(
            method = "init",
            at = @At("HEAD")
    )
    private static void onInitStart(CallbackInfo ci) {
        //棍母棍母棍母棍母棍母棍母棍母棍母棍母棍母
        //棍母棍母棍母棍母棍母棍母棍母棍母棍母棍母
        //棍母棍母棍母棍母棍母棍母棍母棍母棍母棍母
        //棍母棍母棍母棍母棍母棍母棍母棍母棍母棍母棍
    }
}