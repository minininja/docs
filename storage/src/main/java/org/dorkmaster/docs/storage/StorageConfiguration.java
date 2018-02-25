package org.dorkmaster.docs.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

public class StorageConfiguration extends Configuration {
    @NotEmpty
    private String basedir;
    //    @Size(min=1, max=100)
    private int fanout;

    @JsonProperty("swagger")
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    public String getBasedir() {
        return basedir;
    }

    public int getFanout() {
        return fanout;
    }
}
