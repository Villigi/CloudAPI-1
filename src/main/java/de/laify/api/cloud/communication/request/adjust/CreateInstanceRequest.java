package de.laify.api.cloud.communication.request.adjust;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class CreateInstanceRequest extends CloudRequest<Instance> {

    private final String instanceName, instanceGroup;
    private String[] arguments;
    private boolean staticInstance;

    public CreateInstanceRequest(final String instanceName, final String instanceGroup) {
        this.instanceName = instanceName;
        this.instanceGroup = instanceGroup;
        this.staticInstance = false;
        this.arguments = new String[0];
    }

    public CreateInstanceRequest setArguments(final String... arguments) {
        this.arguments = arguments;

        return this;
    }

    public CreateInstanceRequest setStatic(boolean staticInstance) {
        this.staticInstance = staticInstance;

        return this;
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
