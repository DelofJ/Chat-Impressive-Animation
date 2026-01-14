package com.wulian.chatimpressiveanimation.forge;

import com.wulian.chatimpressiveanimation.ChatImpressiveAnimation;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import com.wulian.chatimpressiveanimation.config.ModConfigs;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.client.ConfigGuiHandler;

import java.util.function.Function;

@Mod(value = ChatImpressiveAnimation.MOD_ID)
public class ChatImpressiveAnimationClientForge {
    public ChatImpressiveAnimationClientForge() {
        if (FMLEnvironment.dist.isClient()) {
			ConfigUtil.getConfig();

			registerConfigScreen(ChatImpressiveAnimation.MOD_ID, screen -> AutoConfig.getConfigScreen(ModConfigs.class, screen).get());

			ChatImpressiveAnimation.LOGGER.info("Chat Impressive Animation is loaded!");
        }
    }

	public static void registerConfigScreen(String modid, Function<Screen, Screen> screenFunction) {
		ModContainer modContainer = ModList.get().getModContainerById(modid).orElseThrow();
		modContainer.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
			() -> new ConfigGuiHandler.ConfigGuiFactory((client, screen) -> screenFunction.apply(screen)));
	}
}
