package io.github.archy_x.aureliumskills.stats;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.lang.Lang;
import io.github.archy_x.aureliumskills.lang.Message;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class ActionBar {

	private Plugin plugin;
	
	public ActionBar(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public static HashMap<UUID, Boolean> isGainingXp = new HashMap<UUID, Boolean>();
	public static HashMap<UUID, Integer> timer = new HashMap<UUID, Integer>();
	public static HashMap<UUID, Integer> currentAction = new HashMap<UUID, Integer>();
	
	public void startUpdateActionBar() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (Options.enable_action_bar) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (!currentAction.containsKey(player.getUniqueId())) {
							currentAction.put(player.getUniqueId(), 0);
						}
						if (isGainingXp.containsKey(player.getUniqueId())) {
							if (!isGainingXp.get(player.getUniqueId())) {
								if (Options.enable_health_on_action_bar && Options.enable_mana_on_action_bar) {
										player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP) + 
												"                " + Options.mana_text_color + 20 + "/" + (20 + 2 * SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.WISDOM)) + " " + Lang.getMessage(Message.MANA)));
								}
								else if (Options.enable_health_on_action_bar) {
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.health_text_color + "" + (int) (player.getHealth() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + "/" + (int) (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * Options.getDoubleOption(Setting.HP_INDICATOR_SCALING)) + " " + Lang.getMessage(Message.HP)));
								}
								else if (Options.enable_mana_on_action_bar) {
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Options.mana_text_color + "" + 20 + "/" + (20 + 2 * SkillLoader.playerStats.get(player.getUniqueId()).getStatLevel(Stat.WISDOM)) + " " + Lang.getMessage(Message.MANA)));
								}
							}
						}
						else {
							isGainingXp.put(player.getUniqueId(), false);
						}
					}
				}
			}
		}, 0L, Options.actionBarUpdatePeriod);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (Options.enable_action_bar) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (timer.containsKey(player.getUniqueId())) {
							if (timer.get(player.getUniqueId()) != 0) {
								timer.put(player.getUniqueId(), timer.get(player.getUniqueId()) - 1);
							}
						} else {
							timer.put(player.getUniqueId(), 0);
						}
					}
				}
			}
		}, 0L, 2L);
	}
	
}