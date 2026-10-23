package me.mictoggle;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;

public class MicVoicechatPlugin implements VoicechatPlugin {

    private final MicTogglePlugin plugin;

    public MicVoicechatPlugin(MicTogglePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginId() {
        return "mictoggle";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMic);
    }

    private void onMic(MicrophonePacketEvent event) {
        VoicechatConnection sender = event.getSenderConnection();
        if (sender == null) return;
        if (plugin.isMuted(sender.getPlayer().getUuid())) {
            event.cancel();
        }
    }
}
