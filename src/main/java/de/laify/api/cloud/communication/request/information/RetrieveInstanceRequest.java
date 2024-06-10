package de.laify.api.cloud.communication.request.information;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class RetrieveInstanceRequest extends CloudRequest<Instance> {

    private final String instanceName;

    public RetrieveInstanceRequest(final String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {
        cloudAdapter.retrieveInstanceGroups(msg.getString("instancegroup")).queue(instanceGroup -> {
            Instance instance = Instance.fromResponse(msg);
            instance.setInstanceGroup(instanceGroup.get(0));

            getResponseHandler().accept(instance);
        });
    }
}
