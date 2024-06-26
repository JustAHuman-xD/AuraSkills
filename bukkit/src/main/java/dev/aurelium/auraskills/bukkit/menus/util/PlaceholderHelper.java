package dev.aurelium.auraskills.bukkit.menus.util;

import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.UnitMessage;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class PlaceholderHelper {

    private final AuraSkills plugin;

    public PlaceholderHelper(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public String replaceMenuMessage(String placeholder, Player player, ActiveMenu activeMenu, Replacer replacements) {
        return replaceMenuMessage(placeholder, placeholder, player, activeMenu, replacements);
    }

    public String replaceMenuMessage(String placeholder, String def, Player player, ActiveMenu activeMenu, Replacer replacer) {
        Locale locale = plugin.getUser(player).getLocale();
        // Replace units
        if (placeholder.endsWith("_unit")) {
            for (UnitMessage unitMessage : UnitMessage.values()) {
                if (placeholder.endsWith(unitMessage.toString().toLowerCase(Locale.ROOT) + "_unit")) {
                    return plugin.getMessageProvider().getRaw(unitMessage, locale);
                }
            }
        }
        // Replace menu messages
        if (!placeholder.startsWith("{") || !placeholder.endsWith("}")) {
            return def;
        }
        String stripped = TextUtil.replace(placeholder, "{", "", "}", ""); // Remove curly braces

        MessageKey key = MessageKey.of("menus." + activeMenu.getName().toLowerCase(Locale.ROOT) + "." + stripped);
        String message = plugin.getMessageProvider().getRaw(key, locale);
        // No message found in menu section, try common section
        if (message.equals(key.getPath())) {
            message = plugin.getMessageProvider().getRaw(MessageKey.of("menus.common." + stripped), locale);
        }
        // Replace placeholders
        message = TextUtil.replace(message, replacer);
        return message;
    }

    public String replaceMenuMessages(String source, Player player, ActiveMenu activeMenu, Replacer replacer) {
        String[] placeholders = com.archyx.slate.util.TextUtil.substringsBetween(source, "{", "}");
        if (placeholders == null) return source;

        for (String placeholder : placeholders) {
            String replaced = replaceMenuMessage(placeholder, null, player, activeMenu, replacer);
            // Ignore unplaced placeholders
            if (replaced == null) {
                continue;
            }
            source = TextUtil.replace(source, "{" + placeholder + "}", replaced);
        }
        return source;
    }

}
