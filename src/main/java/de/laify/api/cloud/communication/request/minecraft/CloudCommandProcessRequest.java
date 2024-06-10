package de.laify.api.cloud.communication.request.minecraft;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.CloudRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Getter
public class CloudCommandProcessRequest extends CloudRequest<List<String>> {

    private final String[] commandArgs;

    public CloudCommandProcessRequest(final String[] commandArgs) {
        this.commandArgs = commandArgs;
    }

    @Override
    public void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter) {
        List<String> response = (List<String>) msg.getObject("response");

        getResponseHandler().accept(response);
    }
}
