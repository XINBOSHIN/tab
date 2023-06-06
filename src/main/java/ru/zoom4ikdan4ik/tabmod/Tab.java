package ru.zoom4ikdan4ik.tabmod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mod(modid = "TabMod", name = "TabMod", version = "final")
public class Tab {
	protected float zLevel;

	@EventHandler
	public void load(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void eventHandler(Pre e) {
		if (e.type == ElementType.PLAYER_LIST) {
			e.setCanceled(true);
			Minecraft mc = Minecraft.getMinecraft();
			ScoreObjective scoreobjective = mc.theWorld.getScoreboard().func_96539_a(0);
			NetHandlerPlayClient handler = mc.thePlayer.sendQueue;
			List players = handler.playerInfoList;
			int columns = players.size() <= 25 ? 1
					: (players.size() > 125 ? 5
							: (new BigDecimal((double) players.size() / 25.0D)).setScale(0, RoundingMode.UP)
									.intValue());
			int rows = players.size() <= 25 ? players.size()
					: (new BigDecimal((double) players.size() / (double) columns)).setScale(0, RoundingMode.UP)
							.intValue();
			int columnWidth = (columns == 5 ? 420 : (columns == 4 ? 360 : 300)) / columns;
			if (columnWidth > 150) {
				columnWidth = 150;
			}

			int left = (e.resolution.getScaledWidth() - columns * columnWidth) / 2;
			byte border = 34;
			Gui.drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);

			for (int i = 0; i < players.size(); ++i) {
				int xPos = left + i % columns * columnWidth;
				int yPos = border + i / columns * 9;
				Gui.drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 553648127);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3008);
				if (i < players.size()) {
					GuiPlayerInfo player = (GuiPlayerInfo) players.get(i);
					ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(player.name);
					String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
					mc.fontRenderer.drawStringWithShadow(displayName, xPos, yPos, 16777215);
					if (scoreobjective != null) {
						int endX = xPos + mc.fontRenderer.getStringWidth(displayName) + 5;
						int maxX = xPos + columnWidth - 12 - 5;
						if (maxX - endX > 5) {
							Score score = scoreobjective.getScoreboard().func_96529_a(player.name, scoreobjective);
							String scoreDisplay = "" + EnumChatFormatting.YELLOW + score.getScorePoints();
							mc.fontRenderer.drawStringWithShadow(scoreDisplay,
									maxX - mc.fontRenderer.getStringWidth(scoreDisplay), yPos, 16777215);
						}
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(Gui.icons);
					int pingIndex = 4;
					int ping = player.responseTime;
					if (ping < 0) {
						pingIndex = 5;
					} else if (ping < 150) {
						pingIndex = 0;
					} else if (ping < 300) {
						pingIndex = 1;
					} else if (ping < 600) {
						pingIndex = 2;
					} else if (ping < 1000) {
						pingIndex = 3;
					}

					this.zLevel += 100.0F;
					this.drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8);
					this.zLevel -= 100.0F;
				}
			}
		}

	}

	public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(par1 + 0, par2 + par6, this.zLevel,
				(float) (par3 + 0) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + par6, this.zLevel,
				(float) (par3 + par5) * f, (float) (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + 0, this.zLevel,
				(float) (par3 + par5) * f, (float) (par4 + 0) * f1);
		tessellator.addVertexWithUV(par1 + 0, par2 + 0, this.zLevel,
				(float) (par3 + 0) * f, (float) (par4 + 0) * f1);
		tessellator.draw();
	}
}
