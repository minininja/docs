package org.dorkmaster.docs.index.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.dorkmaster.docs.index.api.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Api("V1 indexer")
@Path("/v1/document")
public class DocumentResource {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String DEFAULT_INDEX = "unindexed";
    private String baseUrl;

    public DocumentResource(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String url(Document doc) {
        return url(doc.getIndex(), doc.getIndex(), doc.getId());
    }

    private String url(String... parts) {
        return baseUrl + "/" + String.join("/", parts);
    }

    private boolean exists(String index) {
        Response response = ClientBuilder.newClient()
                .target(url(index))
                .request()
                .head();
        return 200 == response.getStatus();
    }

    private boolean createIndex(String index, int shards, int replicas) {
        if (!exists(index)) {
            String entity = new JSONObject()
                    .put("settings", new JSONObject()
                            .put("index", new JSONObject()
                                    .put("number_of_shards", shards)
                                    .put("number_of_replicas", replicas)
                            )
                    ).toString();

            String url = url(index);

            WebTarget target = ClientBuilder.newClient()
                    .target(url);
            Response response = target
//                    .queryParam("grr", "grr")
                    .request()
                    .put(Entity.json(entity));
            return 200 == response.getStatus();
        }
        return true;
    }

    @PUT
    @Consumes("application/json")
    @Produces("text/plain")
    public String store(Document doc) {
        logger.info("Document: {}", doc.toString());
        if (StringUtils.isBlank(doc.getId())) {
            doc.setId(UUID.randomUUID().toString());
        }

        if (StringUtils.isBlank(doc.getIndex())) {
            doc.setIndex(DEFAULT_INDEX);
        }

        createIndex(doc.getIndex(), 3, 2);

        try {
            if (exists(doc.getIndex())) {
                Response response = ClientBuilder.newClient()
                        .target(url(doc))
                        .request()
                        .put(Entity.json(new ObjectMapper().writeValueAsString(doc)));

                int code = response.getStatus();
                if (2 == code / 100) {
                    String entity = response.readEntity(String.class);
                    logger.info("index response: [{}] [{}]", code, entity);
                } else {
                    logger.warn("index response: [{}]", code);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        return doc.getId();
    }


}
