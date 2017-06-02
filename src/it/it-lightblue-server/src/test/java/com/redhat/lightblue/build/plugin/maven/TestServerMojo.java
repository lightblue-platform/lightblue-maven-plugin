package com.redhat.lightblue.build.plugin.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.LightblueClientConfiguration;
import com.redhat.lightblue.client.LightblueException;
import com.redhat.lightblue.client.MongoExecution;
import com.redhat.lightblue.client.MongoExecution.ReadPreference;
import com.redhat.lightblue.client.Projection;
import com.redhat.lightblue.client.Query;
import com.redhat.lightblue.client.Update;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.client.request.data.DataDeleteRequest;
import com.redhat.lightblue.client.request.data.DataFindRequest;
import com.redhat.lightblue.client.request.data.DataInsertRequest;
import com.redhat.lightblue.client.request.data.DataUpdateRequest;

public class TestServerMojo {

    private LightblueClient client;

    @Before
    public void before() {
        LightblueClientConfiguration config = new LightblueClientConfiguration();
        config.setDataServiceURI("http://localhost:8000/rest/data");
        config.setMetadataServiceURI("http://localhost:8000/rest/metadata");
        config.setUseCertAuth(false);

        client = new LightblueHttpClient(config);
    }

    @Test
    public void test() throws LightblueException {
        DataInsertRequest insertRequest = new DataInsertRequest(TestEntity.ENTITY_NAME);
        insertRequest.returns(Projection.includeField("_id"));
        insertRequest.create(
                new TestEntity("fake", "created"));
        TestEntity created = client.data(insertRequest, TestEntity.class);

        assertNotNull(created);
        String uuid = created.get_id();
        assertNotNull(uuid);

        TestEntity found = find(client, uuid);

        assertNotNull(found);
        assertEquals("fake", found.getHostname());
        assertEquals("created", found.getValue());

        DataUpdateRequest updateRequest = new DataUpdateRequest(TestEntity.ENTITY_NAME);
        updateRequest.where(Query.withValue("_id", Query.eq, uuid));
        updateRequest.returns(Projection.excludeFieldRecursively("*"));
        updateRequest.updates(Update.set("value", "updated"));
        client.data(updateRequest);

        found = find(client, uuid);

        assertNotNull(found);
        assertEquals("fake", found.getHostname());
        assertEquals("updated", found.getValue());

        DataDeleteRequest deleteRequest = new DataDeleteRequest(TestEntity.ENTITY_NAME);
        deleteRequest.where(
                Query.or(
                        Query.withValue("_id", Query.eq, uuid),
                        Query.withValue("creationDate", Query.lte,
                                Date.from(LocalDateTime
                                        .now()
                                        .minusMinutes(5)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()))
                )
        );
        client.data(deleteRequest);

        assertNull(find(client, uuid));
    }

    private TestEntity find(LightblueClient client, String uuid) throws LightblueException {
        DataFindRequest findRequest = new DataFindRequest(TestEntity.ENTITY_NAME);
        findRequest.select(Projection.includeFieldRecursively("*"));
        findRequest.where(Query.withValue("_id", Query.eq, uuid));
        findRequest.execution(MongoExecution.withReadPreference(ReadPreference.primary));
        return client.data(findRequest, TestEntity.class);
    }

}
