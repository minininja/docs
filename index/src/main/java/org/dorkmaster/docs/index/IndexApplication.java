package org.dorkmaster.docs.index;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.dorkmaster.docs.index.resources.DocumentResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

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

        bootstrap.addBundle(new AssetsBundle("/ui"));
//        bootstrap.addBundle(new AssetsBundle("/ui", "/ui", "index.html"));
    }

    @Override
    public void run(final IndexConfiguration configuration, final Environment environment) {
        environment.jersey().register(new DocumentResource(configuration.getElasticSearchBaseUrl()));

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        ServletRegistration.Dynamic proxy;
        (proxy = environment.servlets().addServlet("proxy", ProxyServlet.class)).addMapping("/index/*");
        proxy.setInitParameter("targetUri", configuration.getElasticSearchBaseUrl());
        proxy.setInitParameter("log", "true");

        // enable cors restrictions
        cors(environment);
    }

    private void cors(Environment environment) {
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

}
