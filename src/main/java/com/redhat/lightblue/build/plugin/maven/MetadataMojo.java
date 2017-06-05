package com.redhat.lightblue.build.plugin.maven;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.redhat.lightblue.build.plugin.MetadataPlugin;
import com.redhat.lightblue.client.LightblueException;
import com.redhat.lightblue.client.PropertiesLightblueClientConfiguration;
import com.redhat.lightblue.client.http.LightblueHttpClient;

@Mojo(name = "metadata", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class MetadataMojo extends AbstractMojo {

    @Parameter(required = true)
    private Map<String, String> metadata;

    @Parameter(defaultValue = "${project.build.directory}/test-classes")
    private String metadataDirectory;

    @Parameter(required = true)
    private String configFilePath;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getMetadataDirectory() {
        return metadataDirectory;
    }

    public void setMetadataDirectory(String metadataDirectory) {
        this.metadataDirectory = metadataDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            getLog().info("Metadata Output Directory: " + getMetadataDirectory());
            new MetadataPlugin(
                    new LightblueHttpClient(
                            PropertiesLightblueClientConfiguration.fromInputStream(new FileInputStream(configFilePath))),
                    getMetadata(), getMetadataDirectory()).run();
        } catch (LightblueException | IOException e) {
            throw new MojoExecutionException("Unable to download metadata from lightblue", e);
        }
    }

}
