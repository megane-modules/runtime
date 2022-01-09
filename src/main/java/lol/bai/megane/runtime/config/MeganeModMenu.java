package lol.bai.megane.runtime.config;

import lol.bai.megane.runtime.config.screen.MeganeConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MeganeModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MeganeConfigScreen::new;
    }

}
