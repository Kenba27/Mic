package me.mictoggle;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MicTogglePlugin extends JavaPlugin implements Listener {

    /** Người chơi đang bị tắt mic (server chặn tiếng của họ). */
    private final Set<UUID> muted = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service == null) {
            getLogger().severe("Không tìm thấy Simple Voice Chat! Plugin sẽ tắt.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        service.registerPlugin(new MicVoicechatPlugin(this));
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MicToggle đã bật.");
    }

    public boolean isMuted(UUID uuid) {
        return muted.contains(uuid);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // Thoát game thì tự bật lại mic, tránh bị tắt vĩnh viễn
        muted.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Chỉ người chơi mới dùng được lệnh này.");
            return true;
        }
        if (args.length != 1) {
            p.sendMessage(Component.text("Dùng: /mic <on|off|toggle>", NamedTextColor.YELLOW));
            return true;
        }

        UUID id = p.getUniqueId();
        boolean wantMuted;
        switch (args[0].toLowerCase()) {
            case "on" -> wantMuted = false;
            case "off" -> wantMuted = true;
            case "toggle" -> wantMuted = !muted.contains(id);
            default -> {
                p.sendMessage(Component.text("Dùng: /mic <on|off|toggle>", NamedTextColor.YELLOW));
                return true;
            }
        }

        if (wantMuted) {
            muted.add(id);
            p.sendActionBar(Component.text("🔇 Mic đã tắt", NamedTextColor.RED));
            p.sendMessage(Component.text("Đã tắt mic. Mọi người sẽ không nghe thấy bạn.", NamedTextColor.RED));
        } else {
            muted.remove(id);
            p.sendActionBar(Component.text("🎤 Mic đã bật", NamedTextColor.GREEN));
            p.sendMessage(Component.text("Đã bật mic.", NamedTextColor.GREEN));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) return List.of("on", "off", "toggle");
        return List.of();
    }
}
