package de.laify.api.cloud.communication.request.log;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LogMessageRequest extends CloudRequest<LogMessageResult> {

    private final String title, message;
    private final int embedColor;
    private final String channelId;
    private final boolean instant;

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {
        getResponseHandler().accept(LogMessageResult.valueOf(msg.getString("result")));
    }
}
