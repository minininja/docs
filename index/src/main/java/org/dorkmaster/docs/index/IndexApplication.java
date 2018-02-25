package org.dorkmaster.docs.index;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.dorkmaster.docs.index.resources.DocumentResource;

public class IndexApplication extends Application<IndexConfiguration> {

    public static void main(final String[] args) throws Exception {
        new IndexApplication().run(args);
    }

    @Override
    public String getName() {
        return "Index";
    }

    @Override
    public void initialize(final Bootstrap<IndexConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<IndexConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(IndexConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });
    }

    @Override
    public void run(final IndexConfiguration configuration, final Environment environment) {
        environment.jersey().register(new DocumentResource(configuration.getElasticSearchBaseUrl()));
    }

}
