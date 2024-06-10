package de.laify.api.cloud.communication.request.information;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

@Getter
public class SendInstanceInformationRequest extends CloudRequest<Void> {

    private final CloudResponse instanceInformation;
    private final String instanceName;

    public SendInstanceInformationRequest(final CloudResponse instanceInformation, final String instanceName) {
        this.instanceInformation = instanceInformation;
        this.instanceName = instanceName;
    }

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {

    }
}
