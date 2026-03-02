package dev.miradiv.golay.mixin;

import dev.miradiv.golay.GoLay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class CheckForSwim
{
    @Inject(method = "isCrawling", at = @At("HEAD"), cancellable = true)
    private void forceSwimming(CallbackInfoReturnable<Boolean> cir)
    {
        if ((Object) this instanceof PlayerEntity player)
        {
            if (GoLay.layingPlayers.getOrDefault(player.getUuid(), false))
            {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void forceUpdateSwimming(CallbackInfo ci)
    {
        if ((Object) this instanceof PlayerEntity player)
        {
            if (GoLay.layingPlayers.getOrDefault(player.getUuid(), false))
            {
                player.setSwimming(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void blockSwimmingReset(boolean swimming, CallbackInfo ci)
    {
        if ((Object) this instanceof PlayerEntity player)
        {
            if (GoLay.layingPlayers.getOrDefault(player.getUuid(), false) && !swimming)
            {
                ci.cancel();
            }
        }
    }
}