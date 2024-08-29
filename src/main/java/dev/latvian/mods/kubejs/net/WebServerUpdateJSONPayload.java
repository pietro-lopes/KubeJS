package dev.latvian.mods.kubejs.net;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public record WebServerUpdateJSONPayload(String event, @Nullable JsonElement payload) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, WebServerUpdateJSONPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, WebServerUpdateJSONPayload::event,
		KubeJSCodecs.JSON_ELEMENT_STREAM_CODEC, WebServerUpdateJSONPayload::payload,
		WebServerUpdateJSONPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.WEB_SERVER_JSON_UPDATE;
	}

	public void handle(IPayloadContext ctx) {
		KubeJSWeb.broadcastUpdate("server/" + event, () -> payload);
	}
}