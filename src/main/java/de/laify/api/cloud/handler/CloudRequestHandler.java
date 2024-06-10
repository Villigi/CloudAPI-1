package de.laify.api.cloud.handler;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CloudRequestHandler extends SimpleChannelInboundHandler<CloudRequest<?>> {

    private final CloudAdapter cloudAdapter;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudRequest<?> msg) {
        if(cloudAdapter.getCloudListener() != null) {
            cloudAdapter.getCloudListener().onCloudRequestReceive(ctx, msg);
        }
    }
}
