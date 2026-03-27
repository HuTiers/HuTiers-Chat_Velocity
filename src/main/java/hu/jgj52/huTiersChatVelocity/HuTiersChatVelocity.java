package hu.jgj52.huTiersChatVelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import hu.jgj52.huTiersMessengerVelocity.Messenger;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

@Plugin(id = "hutiers-chat_velocity", name = "HuTiers-Chat Velocity", version = "1.3", authors = {"JGJ52"}, dependencies = {@Dependency(id = "hutiers-messenger_velocity")})
public class HuTiersChatVelocity {

    private final ProxyServer server;
    private final Logger logger;
    public LuckPerms luckPerms;

    @Inject
    public HuTiersChatVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        luckPerms = LuckPermsProvider.get();
        Messenger.listen("message", message -> {
            String[] args = message.split(" ", 2);
            String uuid = args[0];
            String msg = args[1];
            Optional<Player> playerOpt = server.getPlayer(UUID.fromString(uuid));
            if (playerOpt.isEmpty()) return;
            Player player = playerOpt.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return;
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String name = (metaData.getPrefix() != null ? metaData.getPrefix() : "").replaceAll("&", "§") + player.getUsername() + (metaData.getSuffix() != null ? metaData.getSuffix() : "").replaceAll("&", "§");
            for (Player p : server.getAllPlayers()) {
                p.sendMessage(Component.text(name + ": " + msg));
            }
        });
    }
}
