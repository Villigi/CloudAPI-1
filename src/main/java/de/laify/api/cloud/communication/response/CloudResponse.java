package de.laify.api.cloud.communication.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class CloudResponse implements Serializable {

    private final UUID referenceId;
    private final HashMap<String, Object> responseArguments;

    public CloudResponse(final UUID referenceId) {
        this.referenceId = referenceId;
        this.responseArguments = new HashMap<>();
    }

    public CloudResponse addArgument(final String key, final Object value) {
        this.responseArguments.put(key, value);

        return this;
    }

    public Object getObject(final String key) {
        return this.responseArguments.get(key);
    }

    public String getString(final String key) {
        return (String) getObject(key);
    }

    public Integer getInteger(final String key) {
        return (Integer) getObject(key);
    }
}
