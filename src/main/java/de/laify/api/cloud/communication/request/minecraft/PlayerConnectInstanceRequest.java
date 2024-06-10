package de.laify.api.cloud.communication.request.minecraft;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class PlayerConnectInstanceRequest extends CloudRequest<Instance> {

    private final String playerName, instanceName;

    public PlayerConnectInstanceRequest(final String playerName, final String instanceName) {
        this.playerName = playerName;
        this.instanceName = instanceName;
    }

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {
        Instance instance = Instance.fromResponse(msg);

        cloudAdapter.retrieveInstanceGroups(msg.getString("instancegroup")).queue(instanceGroup -> {
            instance.setInstanceGroup(instanceGroup.get(0));

            getResponseHandler().accept(instance);
        });
    }
}
