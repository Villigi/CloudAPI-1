package de.laify.api.cloud.listener;

import de.laify.api.cloud.communication.request.CloudRequest;
import io.netty.channel.ChannelHandlerContext;

public abstract class CloudListener {

    public void onCloudRequestReceive(ChannelHandlerContext ctx, CloudRequest<?> msg) {}

}
