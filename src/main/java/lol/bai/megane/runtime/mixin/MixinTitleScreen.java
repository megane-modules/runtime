package lol.bai.megane.runtime.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.TranslatableText;

import lol.bai.megane.runtime.util.MeganeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    @Shadow
    private String splashText;

    @Inject(method = "init", at = @At("RETURN"))
    private void showConfigToast(CallbackInfo ci) {
        if (MeganeUtils.showUpdatedConfigToast && splashText != null) {
            SystemToast.show(
                MinecraftClient.getInstance().getToastManager(),
                SystemToast.Type.PACK_COPY_FAILURE,
                new TranslatableText("megane.configToast.title"),
                new TranslatableText("megane.configToast.desc", MeganeUtils.oldConfigVersion, MeganeUtils.CONFIG_VERSION)
            );
            MeganeUtils.showUpdatedConfigToast = false;
        }
    }

}
