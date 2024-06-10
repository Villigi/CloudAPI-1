package de.laify.api.overall.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.*;
import de.laify.LaifyAPI;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Getter
public class DatabaseHandler {

    private final ConnectionString uri;
    private final MongoClientSettings settings;
    private final MongoClient client;
    private final MongoDatabase database;

    private static final int MAX_CONNECTIONS = 20;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public DatabaseHandler(final DatabaseType type) {
        this.uri = new ConnectionString(Objects.requireNonNull(retrieveUrl(type.getIdentifier())));
        this.settings = MongoClientSettings.builder()
                .applyConnectionString(uri)
                .applyToConnectionPoolSettings(builder -> {
                    builder.maxSize(MAX_CONNECTIONS);
                })
                .retryWrites(true).build();
        this.client = createClientWithRetries(settings);
        this.database = client.getDatabase(type.getDatabase());
    }

    public DatabaseAction<DeleteResult> delete(final Bson filter, final String collection) {
        return new DatabaseAction<>(database.getCollection(collection).deleteOne(filter));
    }

    public DatabaseAction<Success> insert(Document document, final String collection) {
        return new DatabaseAction<>(database.getCollection(collection).insertOne(document));
    }

    public DatabaseAction<Document> find(final Bson filter, final String collection) {
        return new DatabaseAction<>(database.getCollection(collection).find(filter));
    }

    public DatabaseAction<Document> findAll(final String collection) {
        return new DatabaseAction<>(database.getCollection(collection).find());
    }

    public DatabaseAction<Document> findAndUpdate(final Bson filter, final Bson update, final String collection) {
        return new DatabaseAction<>(database.getCollection(collection).findOneAndUpdate(filter, update));
    }

    public DatabaseAction<UpdateResult> update(final Bson filter, final Bson update, final String collection) {
        return new DatabaseAction<>(database.getCollection(collection).updateOne(filter, update));
    }

    public void close() {
        this.client.close();
    }

    private MongoClient createClientWithRetries(MongoClientSettings settings) {
        int retryAttempts = 0;
        MongoClient client = null;

        while (retryAttempts < MAX_RETRY_ATTEMPTS) {
            try {
                client = MongoClients.create(settings);
                break;
            } catch (MongoException e) {
                retryAttempts++;
                System.err.println("Fehler beim Herstellen der Verbindung zur MongoDB. Wiederholungsversuch: " + retryAttempts);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (client == null) {
            throw new RuntimeException("Verbindung zur MongoDB konnte nicht hergestellt werden.");
        }

        return client;
    }

    private String retrieveUrl(final String identifier) {
        return "<YOUR MONGODB URL FOR " + identifier + ">";
    }

}
