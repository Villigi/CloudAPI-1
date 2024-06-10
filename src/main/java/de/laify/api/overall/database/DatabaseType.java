package de.laify.api.overall.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseType {

    CLOUD("cloud_admin", "cloud");

    private final String identifier, database;

}
