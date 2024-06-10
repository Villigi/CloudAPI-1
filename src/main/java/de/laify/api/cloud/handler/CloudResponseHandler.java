package de.laify.api.cloud.handler;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class CloudResponseHandler extends SimpleChannelInboundHandler<CloudResponse> {

    private final CloudAdapter cloudAdapter;

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {
        System.out.println("Client connected to server");
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        System.out.println("Client disconnected from server");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudResponse msg) {
        final CloudRequest<?> request = cloudAdapter.getOpenRequests().get(msg.getReferenceId());

        if(msg.getResponseArguments().isEmpty() || request == null || request.getResponseHandler() == null) return;

        request.handleResponse(ctx, msg, cloudAdapter);
        cloudAdapter.getOpenRequests().remove(msg.getReferenceId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
