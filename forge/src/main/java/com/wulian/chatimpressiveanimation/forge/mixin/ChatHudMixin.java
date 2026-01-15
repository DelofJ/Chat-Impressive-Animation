package com.wulian.chatimpressiveanimation.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wulian.chatimpressiveanimation.ChatImpressiveAnimationExpectPlatform;
import com.wulian.chatimpressiveanimation.config.ConfigUtil;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(ChatComponent.class)
public class ChatHudMixin {
	@Shadow private int chatScrollbarPos;
	@Shadow @Final private List<GuiMessage<FormattedCharSequence>> trimmedMessages;
	/*@Shadow public int getLinesPerPage() {
		return this.getHeight() / 9;
	}*/
	@Unique private final ArrayList<Long> messageTimestamps = new ArrayList<>();

	@Unique private final int chatSendingAnimationFadeTime = ConfigUtil.getConfig().chatSendingAnimationFadeTime;
	@Unique private int chatDisplacementY = 0;

	@Unique
	private void calculateYOffset() {
		// Calculate current required offset to achieve slide in from bottom effect
		try {
			int lineHeight = 9;
			// scale * lineHeight
			float fadeOffsetYScale = 0.8f;
			float maxDisplacement = (float)lineHeight * fadeOffsetYScale;
			long timestamp = messageTimestamps.get(0);
			long timeAlive = System.currentTimeMillis() - timestamp;
			if (timeAlive < chatSendingAnimationFadeTime && this.chatScrollbarPos == 0) {
				chatDisplacementY = (int)(maxDisplacement - (((float) timeAlive / chatSendingAnimationFadeTime) * maxDisplacement));
			} else {
				chatDisplacementY = 0;
			}
		} catch (Exception ignored) {
			chatDisplacementY = 0;
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRenderStart(PoseStack arg, int i, CallbackInfo ci) {
		if (!ConfigUtil.getConfig().enableChatSendingAnimation) return;
		calculateYOffset();

		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}

		arg.translate(0, chatDisplacementY + raisedOffset, 0);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderEnd(PoseStack arg, int i, CallbackInfo ci) {
		// Apply Raised mod compatibility
		float raisedOffset = 0;
		if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:hud") instanceof Integer distance) {
			raisedOffset -= distance;
		} else if (ChatImpressiveAnimationExpectPlatform.getObjectShareItem("raised:distance") instanceof Integer distance) {
			raisedOffset -= distance;
		}

		arg.translate(0, -(chatDisplacementY + raisedOffset), 0);
	}

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", at = @At("TAIL"))
	private void addMessage(Component arg, int i, int j, boolean bl, CallbackInfo ci) {
		messageTimestamps.add(0, System.currentTimeMillis());
		while (this.messageTimestamps.size() > this.trimmedMessages.size()) {
			this.messageTimestamps.remove(messageTimestamps.size() - 1);
		}
	}
}
