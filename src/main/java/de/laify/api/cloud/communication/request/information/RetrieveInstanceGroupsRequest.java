package de.laify.api.cloud.communication.request.information;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import de.laify.api.cloud.instance.InstanceGroup;
import de.laify.api.cloud.instance.InstanceType;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RetrieveInstanceGroupsRequest extends CloudRequest<List<InstanceGroup>> {

    private final String[] instanceGroupNames;

    public RetrieveInstanceGroupsRequest(final String... instanceGroupNames) {
        this.instanceGroupNames = instanceGroupNames;
    }

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {
        final List<InstanceGroup> instanceGroups = new ArrayList<>();

        for(CloudResponse groupResponse : ((List<CloudResponse>) msg.getObject("groups"))) {
            List<Instance> instances = new ArrayList<>();

            for(CloudResponse response : ((List<CloudResponse>) groupResponse.getObject("instances"))) {
                instances.add(Instance.fromResponse(response));
            }

            InstanceGroup instanceGroup = new InstanceGroup(
                    groupResponse.getString("name"),
                    groupResponse.getInteger("ram"),
                    groupResponse.getInteger("minInstances"),
                    groupResponse.getInteger("maxInstances"),
                    instances,
                    InstanceType.valueOf(groupResponse.getString("instanceType"))
            );

            for (Instance instance : instances) {
                instance.setInstanceGroup(instanceGroup);
            }

            instanceGroups.add(instanceGroup);
        }

        getResponseHandler().accept(instanceGroups);
    }
}
