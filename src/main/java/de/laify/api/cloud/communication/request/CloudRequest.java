package de.laify.api.cloud.communication.request;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.response.CloudResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public abstract class CloudRequest<T> implements Serializable {

    private transient Consumer<T> responseHandler;
    private transient int timeoutSeconds;

    private final UUID id;

    public CloudRequest() {
        this.id = UUID.randomUUID();
    }

    public abstract void handleResponse(ChannelHandlerContext ctx, CloudResponse msg, CloudAdapter cloudAdapter);

    public void queue(final Consumer<T> responseHandler, final int timeoutSeconds) {
        if(responseHandler != null) {
            this.responseHandler = responseHandler;
            this.timeoutSeconds = timeoutSeconds;
        }

        if(CloudAdapter.getInstance() != null) {
            CloudAdapter.getInstance().queue(this);
        }
    }

    public void queue(final Consumer<T> responseHandler) {
        queue(responseHandler, 30);
    }

    public void queue() {
        queue(null, 0);
    }

}
