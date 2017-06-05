package com.redhat.lightblue.build.plugin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.LightblueException;
import com.redhat.lightblue.client.request.metadata.MetadataGetEntityMetadataRequest;
import com.redhat.lightblue.client.response.LightblueMetadataResponse;

public class MetadataPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataPlugin.class);

    private final LightblueClient lightblueClient;
    /** key=name of metadata, value=version, if no version than use default */
    private final Map<String, String> metadata;

    private final String metadataDirectory;

    public MetadataPlugin(LightblueClient lightblueClient, Map<String, String> metadata, String metadataDirectory) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("metadata: " + metadata.toString());
            LOGGER.debug("metadataDirectory: " + metadataDirectory);
        }

        this.lightblueClient = lightblueClient;
        this.metadata = metadata;
        this.metadataDirectory = metadataDirectory;
    }

    public void run() throws LightblueException, IOException {

        for (Entry<String, String> entity : metadata.entrySet()) {
            String entityName = entity.getKey();
            String entityVersion = entity.getValue();

            LightblueMetadataResponse response = lightblueClient.metadata(
                    new MetadataGetEntityMetadataRequest(entityName, entityVersion));

            try (FileWriter writer = new FileWriter(metadataDirectory + "/" + entityName + ".json")) {
                writer.write(response.getText());
            }
        }

    }

}
