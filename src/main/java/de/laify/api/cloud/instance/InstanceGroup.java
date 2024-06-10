package de.laify.api.cloud.instance;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InstanceGroup {

    private final String name;
    private final int ram, minInstances, maxInstances;
    private final List<Instance> instances;
    private final InstanceType instanceType;

}
