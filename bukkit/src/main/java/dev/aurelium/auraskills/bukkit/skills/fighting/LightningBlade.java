package dev.aurelium.auraskills.bukkit.skills.fighting;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class LightningBlade extends ReadiedManaAbility {

    private final UUID MODIFIER_ID = UUID.fromString("2fc64528-614b-11ee-8c99-0242ac120002");

    public LightningBlade(AuraSkills plugin) {
        super(plugin, ManaAbilities.LIGHTNING_BLADE, ManaAbilityMessage.LIGHTNING_BLADE_START, ManaAbilityMessage.LIGHTNING_BLADE_END,
                new String[]{"SWORD"}, new Action[]{Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK});
    }

    @Override
    public void onActivate(Player player, User user) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute == null) return;

        // Remove existing modifier if exists
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getName().equals("auraskills_lightning_blade")) {
                attribute.removeModifier(modifier);
            }
        }
        // Increase attack speed attribute
        double addedValue = getValue(user) / 100;
        attribute.addModifier(new AttributeModifier(MODIFIER_ID, "auraskills_lightning_blade", addedValue, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        // Play sound and send message
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.5f, 1);
    }

    @Override
    public void onStop(Player player, User user) {
        user.removeTraitModifier("lightning_blade");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void activationListener(EntityDamageByEntityEvent event) {
        if (isDisabled()) return;
        if (event.isCancelled()) return;

        if (!(event.getDamager() instanceof Player player)) return;
        if (failsChecks(player)) return;
        //If player used sword
        if (!isHoldingMaterial(player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        DamageCause cause = event.getCause();
        if (cause == DamageCause.ENTITY_SWEEP_ATTACK || cause == DamageCause.THORNS) return; // Ignore sweeping attacks
        // Checks if already activated
        User user = plugin.getUser(player);
        if (isActivated(user)) {
            return;
        }
        // Checks if ready
        checkActivation(player);
    }

    @Override
    protected int getDuration(User user) {
        double baseDuration = manaAbility.optionDouble("base_duration");
        double durationPerLevel = manaAbility.optionDouble("duration_per_level");
        double durationSeconds = baseDuration + (durationPerLevel * (user.getManaAbilityLevel(manaAbility) - 1));
        return (int) Math.round(durationSeconds * 20);
    }

    @EventHandler
    public void lightningBladeJoin(PlayerJoinEvent event) {
        // Only remove if not activated
        Player player = event.getPlayer();
        if (isActivated(plugin.getUser(player))) {
            return;
        }
        // Remove attack speed attribute modifier
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute == null) return;
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getName().equals("AureliumSkills-LightningBlade")) {
                attribute.removeModifier(modifier);
            }
        }
    }
}
