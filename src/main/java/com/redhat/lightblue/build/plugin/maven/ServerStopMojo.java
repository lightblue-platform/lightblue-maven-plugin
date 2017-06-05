package com.redhat.lightblue.build.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.redhat.lightblue.build.plugin.maven.ServerStartMojo.Hack;
import com.redhat.lightblue.mongo.test.LightblueMongoTestHarness;
import com.redhat.lightblue.rest.integration.LightblueRestTestHarness;

@Mojo(name = "server-stop", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class ServerStopMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Stopping lightblue server");
        LightblueRestTestHarness.stopHttpServer();
        ((Hack) LightblueMongoTestHarness.mongoServer).after();
    }

}
