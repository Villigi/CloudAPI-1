package de.laify.api.cloud.communication.request.status;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import de.laify.api.cloud.communication.request.CloudRequest;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class InstanceStatusChangeRequest extends CloudRequest<Instance> {

    private final String instanceName;
    private final InstanceStatusType statusType;
    private final int instancePort;

    public InstanceStatusChangeRequest(final String instanceName, final int instancePort, final InstanceStatusType statusType) {
        this.instanceName = instanceName;
        this.instancePort = instancePort;
        this.statusType = statusType;
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
