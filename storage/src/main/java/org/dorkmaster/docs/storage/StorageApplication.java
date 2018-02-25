package org.dorkmaster.docs.storage;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.dorkmaster.docs.storage.resources.StorageResource;

public class StorageApplication extends Application<StorageConfiguration> {

    public static void main(final String[] args) throws Exception {
        new StorageApplication().run(args);
    }

    @Override
    public String getName() {
        return "Storage API";
    }

    @Override
    public void initialize(final Bootstrap<StorageConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<StorageConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(StorageConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });
    }

    @Override
    public void run(final StorageConfiguration configuration, final Environment environment) {
        environment.jersey().register(new StorageResource(configuration.getBasedir(), configuration.getFanout()));
    }

}
