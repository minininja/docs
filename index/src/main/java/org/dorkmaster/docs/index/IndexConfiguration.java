package org.dorkmaster.docs.index;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class IndexConfiguration extends Configuration {
    @JsonProperty
    private String elasticSearchBaseUrl = "http://localhost:9200";

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    public String getElasticSearchBaseUrl() {
        return elasticSearchBaseUrl;
    }

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }
}
