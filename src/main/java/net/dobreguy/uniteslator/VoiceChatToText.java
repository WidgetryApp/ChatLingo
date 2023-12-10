package net.dobreguy.uniteslator;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.dobreguy.uniteslator.config.UniteslatorConfig;
import org.jetbrains.annotations.Nullable;
import org.vosk.Model;

public class VoiceChatToText  implements VoicechatPlugin {
    public static VoicechatApi voicechatApi;
    /** The following variables are used to store the microphone handler*/

    /** The following variables are used to store the last recognized result*/
    private static String lastResult = "";

    @Nullable
    public static VoicechatServerApi voicechatServerApi;

    @Nullable
    private OpusDecoder decoder;
    @Override
    public String getPluginId() {
        return Uniteslator.MOD_ID;
    }
    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatServerApi = event.getVoicechat();
    }

    private void onMicPacket(MicrophonePacketEvent event)  {
        try{
            VoicechatConnection senderConnection = event.getSenderConnection();
            if (senderConnection == null) {
                return;
            }

            if (event.getPacket().getOpusEncodedData().length <= 0) {
                // Don't trigger any events when stopping to talk
                return;
            }

            if (decoder == null) {
                decoder = event.getVoicechat().createDecoder();
            }


            decoder.resetState();
            byte[] encodedData = event.getPacket().getOpusEncodedData();
            short[] decoded = decoder.decode(encodedData);

            Uniteslator.LOGGER.info(decoded.toString());
        }
        catch (Exception e){
            Uniteslator.LOGGER.info(e.toString());
        }

    }

}