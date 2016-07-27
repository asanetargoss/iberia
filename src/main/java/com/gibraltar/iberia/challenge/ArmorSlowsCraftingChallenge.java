package com.gibraltar.iberia.challenge;

import java.io.IOException;
import java.util.List;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.Reference;

public class ArmorSlowsCraftingChallenge extends Challenge {
	private long timeGuiOpened;
	private long armorDelayMs;
	private int leatherDelay;
	private int ironDelay;
	private int chainDelay;
	private int goldDelay;
	private int diamondDelay;

	public boolean hasSubscriptions() {
		return true;
	}

	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "LeatherDelay", 200);
        leatherDelay = prop.getInt(200);
		prop = config.get(name, "IronDelay", 600);
        ironDelay = prop.getInt(600);
		prop = config.get(name, "ChainDelay", 600);
        chainDelay = prop.getInt(600);
		prop = config.get(name, "GoldDelay", 200);
        goldDelay = prop.getInt(200);
		prop = config.get(name, "DiamondDelay", 1200);
        diamondDelay = prop.getInt(1200);
    }


	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
	    if (isSlowGui(event.getGui())) {
			timeGuiOpened = Minecraft.getSystemTime();
			Iterable<ItemStack> armorInventory = Minecraft.getMinecraft().thePlayer.getArmorInventoryList();
			armorDelayMs = 0;

			for (Object item : armorInventory) {
                ItemStack stack = (ItemStack) item;
                if (stack == null) {
                    continue;
                }
				if (stack.getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor)stack.getItem();
					switch (armor.getArmorMaterial()) {
						case LEATHER:
							armorDelayMs += leatherDelay;
							break;
						case IRON:
							armorDelayMs += ironDelay;
							break;
						case CHAIN:
							armorDelayMs += chainDelay;
							break;
						case GOLD:
							armorDelayMs += goldDelay;
							break;
						case DIAMOND:
							armorDelayMs += diamondDelay;
							break;

					}
				}
			}
	    }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
	    if (isSlowGui(event.getGui())) {
			if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime()) {
				double visibility = (Minecraft.getSystemTime() - timeGuiOpened) / (double)armorDelayMs;
				int alpha = (int)Math.round(64.0D - visibility * 64.0D);

				GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
				GlStateManager.enableBlend();

				Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();
				int guiWidth = event.getGui().width;
				int guiHeight = event.getGui().height;
				int guiDrawnHeight = guiDrawnHeight(event.getGui());
				int guiDrawnWidth = guiDrawnWidth(event.getGui());
                int guiLeft = (guiWidth - guiDrawnWidth) / 2;
				int guiTop = (guiHeight - guiDrawnHeight) / 2;
				this.draw(vertexbuffer, guiLeft, guiTop, guiDrawnWidth, guiDrawnHeight, 255, 255, 255, 128);
				int guiProgressLeft = guiLeft + (guiDrawnWidth - 16) / 2;
				int guiProgressTop = guiTop + (guiDrawnHeight - 86);
				this.draw(vertexbuffer, guiProgressLeft, guiProgressTop, 16, 2, 0, 0, 0, 128);
				this.draw(vertexbuffer, guiProgressLeft, guiProgressTop, (int)Math.round(16.0D * visibility), 2, 255, 255, 255, 255);

                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();				
			}
	    }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
	    if (isSlowGui(event.getGui())) {
			if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime()) {
				event.setCanceled(true);
			}
	    }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
	    if (isSlowGui(event.getGui())) {
			if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime() && (Keyboard.getEventKey() != 1)) {
				event.setCanceled(true);
			}
	    }
	}

	private boolean isSlowGui(GuiScreen gui) {
		return gui instanceof GuiContainer &&
			!(gui instanceof InventoryEffectRenderer) &&
			!(gui instanceof GuiMerchant) &&
			!(gui instanceof GuiScreenHorseInventory);
	}

	private int guiDrawnHeight(GuiScreen gui) {
		if (gui instanceof GuiHopper) {
			return 133;
		}
		if (gui instanceof GuiBeacon) {
			return 219;
		}

		return 166;
	}

	private int guiDrawnWidth(GuiScreen gui) {
		if (gui instanceof GuiBeacon) {
			return 230;
		}

		return 176;
	}

    private void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }
}