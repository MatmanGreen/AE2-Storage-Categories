package de.gruen.ae2categories.mixin;

import appeng.client.gui.AEBaseScreen;
import de.gruen.ae2categories.HeaderInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEBaseScreen.class, remap = false)
public abstract class HeaderGestureMixin {
    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void categories$release(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof HeaderInteraction header && header.categories$releaseHeader(button)) cir.setReturnValue(true);
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void categories$drag(double x, double y, int button, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof HeaderInteraction header && header.categories$headerGesture()) cir.setReturnValue(true);
    }
}
