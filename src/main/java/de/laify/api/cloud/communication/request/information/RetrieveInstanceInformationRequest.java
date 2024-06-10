package de.laify.api.cloud.communication.request.information;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import de.laify.api.cloud.instance.Instance;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class RetrieveInstanceInformationRequest extends CloudRequest<CloudResponse> {

    private final String ownInstanceName, instanceName;

    public RetrieveInstanceInformationRequest(final String ownInstanceName, final String instanceName) {
        this.ownInstanceName = ownInstanceName;
        this.instanceName = instanceName;
    }

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {
        getResponseHandler().accept(msg);
    }
}
