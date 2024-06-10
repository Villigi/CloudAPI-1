package de.laify.api.cloud.communication.request.adjust;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ModifyInstanceRequest extends CloudRequest<Instance> {

    private final String instanceName;
    private final InstanceModification instanceModification;

    public ModifyInstanceRequest(final String instanceName, final InstanceModification instanceModification) {
        this.instanceName = instanceName;
        this.instanceModification = instanceModification;
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
