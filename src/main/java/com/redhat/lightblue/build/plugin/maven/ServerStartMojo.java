package com.redhat.lightblue.build.plugin.maven;

import static com.redhat.lightblue.util.JsonUtils.json;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.mongo.test.LightblueMongoTestHarness;
import com.redhat.lightblue.mongo.test.MongoServerExternalResource;
import com.redhat.lightblue.rest.integration.LightblueRestTestHarness;

@Mojo(name = "server-start", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class ServerStartMojo extends AbstractMojo {

    @Parameter(defaultValue = "8000")
    private Integer httpServerPort;

    @Parameter(required = true)
    private String[] metadataPaths;

    @Parameter(required = true)
    private String datasourceName;

    @Parameter(defaultValue = "datasources.json")
    private String datasourcesJsonPath;

    @Parameter(defaultValue = "lightblue-crud.json")
    private String crudJsonPath;

    @Parameter(defaultValue = "lightblue-metadata.json")
    private String metadataJsonPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting lightblue server");
        try {
            Hack hack = new Hack();
            hack.before();
            LightblueMongoTestHarness.mongoServer = hack;
            LightblueMongoTestHarness.prepareMongoDatasources();
            new LightblueRestTestHarness(httpServerPort) {

                @Override
                protected JsonNode[] getMetadataJsonNodes() throws Exception {
                    return Arrays.stream(metadataPaths)
                            .distinct()
                            .map(path -> {
                                try {
                                    getLog().info("Loading Metadata: " + path);
                                    return json(new FileInputStream(path));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .toArray(JsonNode[]::new);
                }

                @Override
                protected String getDatasource() {
                    getLog().info("Datasource Name: " + datasourceName);
                    return datasourceName;
                }

                @Override
                protected JsonNode getLightblueCrudJson() throws Exception {
                    getLog().info("Loading CRUD Config: " + crudJsonPath);
                    return json(new FileInputStream(crudJsonPath));
                }

                @Override
                protected JsonNode getLightblueMetadataJson() throws Exception {
                    getLog().info("Loading Metadata Config: " + metadataJsonPath);
                    return json(new FileInputStream(metadataJsonPath));
                }

                @Override
                protected JsonNode getDatasourcesJson() throws Exception {
                    getLog().info("Loading Datasources Config: " + datasourcesJsonPath);
                    return json(new FileInputStream(datasourcesJsonPath));
                }

            };
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to start lightblue", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                getLog().info("Stopping lightblue server");
                LightblueRestTestHarness.stopHttpServer();
            }

        });
    }

    static class Hack extends MongoServerExternalResource {
        @Override
        public void before() throws IOException {
            super.before();
        }

        @Override
        public void after() {
            super.after();
        }
    }

}
