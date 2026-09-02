package com.taskmanager.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import net.javacrumbs.shedlock.core.LockProvider;

class ShedLockConfigTest {

    @Test
    @SuppressWarnings("unchecked")
    void lockProvider_createsProviderUsingTaskdb() {

        MongoClient mongoClient =
                mock(MongoClient.class);

        MongoDatabase mongoDatabase =
                mock(MongoDatabase.class);

        MongoCollection<Document> collection =
                mock(MongoCollection.class);

        when(mongoClient.getDatabase("taskdb"))
                .thenReturn(mongoDatabase);

        /*
         * Don't hard-code the collection name in the mock.
         * Whatever String the MongoProvider requests,
         * return our mocked collection.
         */
        when(mongoDatabase.getCollection(anyString()))
                .thenReturn(collection);

        when(collection.withWriteConcern(WriteConcern.MAJORITY))
                .thenReturn(collection);

        ShedLockConfig config =
                new ShedLockConfig();

        LockProvider provider =
                config.lockProvider(mongoClient);

        assertNotNull(provider);

        verify(mongoClient)
                .getDatabase("taskdb");
    }
}
