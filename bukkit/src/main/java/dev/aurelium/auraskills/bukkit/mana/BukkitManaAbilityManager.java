package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.archery.ChargedShot;
import dev.aurelium.auraskills.bukkit.skills.defense.Absorption;
import dev.aurelium.auraskills.bukkit.skills.excavation.Terraform;
import dev.aurelium.auraskills.bukkit.skills.farming.Replenish;
import dev.aurelium.auraskills.bukkit.skills.fighting.LightningBlade;
import dev.aurelium.auraskills.bukkit.skills.fishing.SharpHook;
import dev.aurelium.auraskills.bukkit.skills.foraging.Treecapitator;
import dev.aurelium.auraskills.bukkit.skills.mining.SpeedMine;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BukkitManaAbilityManager extends ManaAbilityManager {

    private final AuraSkills plugin;
    private final Map<Class<?>, ManaAbilityProvider> providerMap;

    public BukkitManaAbilityManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        this.providerMap = new HashMap<>();
    }

    public void registerProviders() {
        new TimerCountdown(plugin); // Start counting down cooldown and error timers
        registerProvider(new Replenish(plugin));
        registerProvider(new Treecapitator(plugin));
        registerProvider(new SpeedMine(plugin));
        registerProvider(new SharpHook(plugin));
        registerProvider(new Terraform(plugin));
        registerProvider(new ChargedShot(plugin));
        registerProvider(new Absorption(plugin));
        registerProvider(new LightningBlade(plugin));
    }

    private void registerProvider(ManaAbilityProvider provider) {
        providerMap.put(provider.getClass(), provider);
        Bukkit.getPluginManager().registerEvents(provider, plugin);
    }

    public <T extends ManaAbilityProvider> T getProvider(Class<T> clazz) {
        ManaAbilityProvider provider = providerMap.get(clazz);
        if (provider != null) {
            return clazz.cast(provider);
        }
        throw new IllegalArgumentException("Mana ability provider of type " + clazz.getSimpleName() + " not found!");
    }

    @Nullable
    public ManaAbilityProvider getProvider(ManaAbility manaAbility) {
        for (ManaAbilityProvider provider : providerMap.values()) {
            if (provider.getManaAbility().equals(manaAbility)) {
                return provider;
            }
        }
        return null;
    }

    @Override
    public void sendNotEnoughManaMessage(User user, double manaCost) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return;
        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(ManaAbilityMessage.NOT_ENOUGH_MANA, user.getLocale())
                ,"{mana}", NumberUtil.format0(manaCost)
                , "{current_mana}", String.valueOf(Math.round(user.getMana()))
                , "{max_mana}", String.valueOf(Math.round(user.getMaxMana()))));
    }

    @Override
    public String getBaseDescription(ManaAbility manaAbility, User user) {
        String desc = manaAbility.getDescription(user.getLocale());
        ManaAbilityProvider provider = plugin.getManaAbilityManager().getProvider(manaAbility);
        if (provider != null) {
            desc = provider.replaceDescPlaceholders(desc, user);
        }
        return desc;
    }

}
