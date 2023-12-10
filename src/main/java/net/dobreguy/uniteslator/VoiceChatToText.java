package net.dobreguy.uniteslator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;


public class VoiceChatToText  implements VoicechatPlugin {
    public static VoicechatApi voicechatApi;
    /** The following variables are used to store the microphone handler*/

    /** The following variables are used to store the last recognized result*/
    private int energyThreshold = 1000;
    private float recordTimeout = 2.0f;
    private float phraseTimeout = 3.0f;
    private LinkedBlockingQueue<byte[]> dataQueue = new LinkedBlockingQueue<>();
    private volatile long phraseTime = 0;
    private volatile boolean stopRecognition = false;

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

        Uniteslator.LOGGER.info("Listening to do transcriptions");
    }



    private void onMicPacket(MicrophonePacketEvent event)  {
        try{
            VoicechatConnection senderConnection = event.getSenderConnection();
            if (senderConnection == null) {
                return;
            }

            if (event.getPacket().getOpusEncodedData().length <= 0) {
                return;
            }

            if (decoder == null) {
                decoder = event.getVoicechat().createDecoder();
            }

            long now = System.currentTimeMillis();
            if (now - phraseTime > phraseTimeout * 1000) {
                processAudioData(true);
            }


            decoder.resetState();
            byte[] encodedData = event.getPacket().getOpusEncodedData();

            dataQueue.offer(Arrays.copyOf(encodedData, encodedData.length));

            short[] decoded = decoder.decode(encodedData);

            Uniteslator.LOGGER.info(decoded.toString());
        }
        catch (Exception e){
            Uniteslator.LOGGER.info(e.toString());
        }

    }
    private void processAudioData(boolean phraseComplete) {
        long now = System.currentTimeMillis();

        if (!dataQueue.isEmpty()) {
            byte[] audioData = concatenateAudioData();
            dataQueue.clear();

            // Convert audio data to float array
             short[] audioFloat = new short[audioData.length / 2];
            ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioFloat);

            // Perform transcription using the audio data
            // Replace this part with your transcription logic
            String transcription = performTranscription(audioFloat);

            if (phraseComplete) {
                System.out.println(transcription);
            } else {
                // Update the existing transcription
                updateTranscription(transcription);
            }

            phraseTime = now;
        }
    }

    private byte[] concatenateAudioData() {
        int totalSize = 0;
        for (byte[] data : dataQueue) {
            totalSize += data.length;
        }

        byte[] result = new byte[totalSize];
        int offset = 0;

        for (byte[] data : dataQueue) {
            System.arraycopy(data, 0, result, offset, data.length);
            offset += data.length;
        }

        return result;
    }

    private String performTranscription(short[] audioFloat) {
        Uniteslator.LOGGER.info("Do the transcription here");
        return "Transcription Placeholder";
    }

    private void updateTranscription(String transcription) {
        // Replace this with your transcription update logic
        System.out.println(transcription);
    }

    public void stopRecognition() {
        stopRecognition = true;
    }

}