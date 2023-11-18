package net.dobreguy.uniteslator.mixin;

import net.dobreguy.uniteslator.UniteslatorClient;
import net.dobreguy.uniteslator.accessor.AbstractClientPlayerEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dobreguy.uniteslator.Uniteslator.LOGGER;

@Environment(EnvType.CLIENT)
@Mixin(ChatHud.class)
public class ChatHudMixin {
    private static final Pattern p = Pattern.compile("class=\"result-container\">([^<]*)<\\/div>", Pattern.MULTILINE);
    private static final Pattern NO_USERNAME = Pattern.compile("<(.+?)> (.+)", Pattern.MULTILINE);
    @Shadow
    @Final
    @Mutable
    private MinecraftClient client;

    // onChatMessage is now done in MessageHandler.class
    @Inject(method = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"))
    private void addMessageMixin(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo info) {
        if (client != null && client.player != null && !extractSender(message).isEmpty()) {
            String detectedSenderName = extractSender(message);
            UUID senderUUID = this.client.getSocialInteractionsManager().getUuid(detectedSenderName);
            List<AbstractClientPlayerEntity> list = client.world.getEntitiesByClass(AbstractClientPlayerEntity.class, client.player.getBoundingBox().expand(UniteslatorClient.CONFIG.chatRange),
                    EntityPredicates.EXCEPT_SPECTATOR);

            if (!UniteslatorClient.CONFIG.showOwnBubble)
                list.remove(client.player);
            for (int i = 0; i < list.size(); i++)
                if (list.get(i).getUuid().equals(senderUUID)) {
                    String stringMessage = TranslateMessage(message.getString());
                    stringMessage = stringMessage.replaceFirst("[\\s\\S]*" + detectedSenderName + "([^\\w§]|(§.)?)+\\s+", "");
                    String[] string = stringMessage.split(" ");
                    List<String> stringList = new ArrayList<>();
                    String stringCollector = "";

                    int width = 0;
                    int height = 0;
                    for (int u = 0; u < string.length; u++) {
                        if (client.textRenderer.getWidth(stringCollector) < UniteslatorClient.CONFIG.maxChatWidth
                                && client.textRenderer.getWidth(stringCollector) + client.textRenderer.getWidth(string[u]) <= UniteslatorClient.CONFIG.maxChatWidth) {
                            stringCollector = stringCollector + " " + string[u];
                            if (u == string.length - 1) {
                                stringList.add(stringCollector);
                                height++;
                                if (width < client.textRenderer.getWidth(stringCollector))
                                    width = client.textRenderer.getWidth(stringCollector);
                            }
                        } else {
                            stringList.add(stringCollector);

                            height++;
                            if (width < client.textRenderer.getWidth(stringCollector))
                                width = client.textRenderer.getWidth(stringCollector);

                            stringCollector = string[u];

                            if (u == string.length - 1) {
                                stringList.add(stringCollector);
                                height++;
                                if (width < client.textRenderer.getWidth(stringCollector))
                                    width = client.textRenderer.getWidth(stringCollector);
                            }
                        }
                    }

                    if (width % 2 != 0)
                        width++;
                    ((AbstractClientPlayerEntityAccessor) list.get(i)).setChatText(stringList, list.get(i).age, width, height);
                    break;
                }
            }
        }

    private String extractSender(Text text) {
        String[] words = text.getString().split("(§.)|[^\\w§]+");

        for (String word : words) {
            if (word.isEmpty())
                continue;

            UUID possibleUUID = this.client.getSocialInteractionsManager().getUuid(word);
            if (possibleUUID != Util.NIL_UUID) {
                return word;
            }
        }

        return "";
    }

    private String TranslateMessage(String msg){
        //remove username
        Matcher mu = NO_USERNAME.matcher(msg);
        mu.find();
        try {
            msg = mu.group(2);
        } catch (Exception e) {
            //oops bad chat format ???
            System.out.println("ERROR ! Bad chat format !");
        }

        String enc = "";
        try {
            enc = URLEncoder.encode(msg, "utf-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        try {
            URL u = new URL("https://translate.google.com/m?sl=auto&tl=" + client.getLanguageManager().getLanguage().split("_")[0] + "&hl=en&ie=UTF-8&prev=_m&&q=" + enc);
            HttpURLConnection con = ((HttpURLConnection) u.openConnection());
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            con.connect();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Matcher m = p.matcher(sb.toString());
            m.find();
            return StringEscapeUtils.unescapeHtml4(m.group(1));
        } catch (IOException e) {
            return "";
        }
    }
}
