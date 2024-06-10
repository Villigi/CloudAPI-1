package de.laify.api.cloud.instance;

import de.laify.api.cloud.CloudAdapter;
import de.laify.api.cloud.communication.request.information.RetrieveInstanceInformationRequest;
import de.laify.api.cloud.communication.response.CloudResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Instance {

    private final String name, realName;

    @Setter
    private InstanceGroup instanceGroup;
    private final InstanceState instanceState;

    public RetrieveInstanceInformationRequest retrieveInformation() {
        return CloudAdapter.getInstance().retrieveInstanceInformation(getName());
    }

    public static Instance fromResponse(final CloudResponse response) {
        return new Instance(response.getString("name"), response.getString("realname"), null, InstanceState.valueOf(response.getString("instancestate")));
    }

}
