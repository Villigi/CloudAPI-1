package de.laify;

import de.laify.api.discord.DiscordHook;
import de.laify.api.overall.database.DatabaseHandler;
import de.laify.api.overall.database.DatabaseType;
import lombok.Getter;

import java.util.HashMap;

public class LaifyAPI {

    @Getter
    private static boolean loaded;
    @Getter
    private static HashMap<DatabaseType, DatabaseHandler> databaseHandlers;
    @Getter
    private static DiscordHook discordHook;

    @Getter
    private static final boolean local = System.getProperty("os.name").toLowerCase().contains("win");

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        if(loaded)return;

        loaded = true;
        databaseHandlers = new HashMap<>();
    }

    public static DiscordHook createDiscordHook() {
        return discordHook = new DiscordHook();
    }

    public static void terminate() {
        databaseHandlers.forEach((key, value) -> value.close());
    }

    public static DatabaseHandler getDatabaseHandler(final DatabaseType databaseType) {
        return databaseHandlers.computeIfAbsent(databaseType, DatabaseHandler::new);
    }
}
