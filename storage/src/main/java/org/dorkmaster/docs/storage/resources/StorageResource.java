package org.dorkmaster.docs.storage.resources;

import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Api
@Path("/v1/storage")
@Consumes("application/octet-stream")
public class StorageResource {
    private String basedir;
    private int fanout;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public StorageResource(String basedir, int fanout) {
        this.basedir = basedir;
        this.fanout = fanout;
    }

    @GET
    @Path("/{bucket}/{fileId}")
    @Produces("application/octet-stream")
    public Response read(@Context @PathParam("bucket") String bucket, @PathParam("fileId") String fileId, @QueryParam("filename") String name) throws FileNotFoundException {
        File file = file(bucket, fileId);
        if (file.exists() && file.canRead()) {
            Response.ResponseBuilder rb = Response.ok(new FileInputStream(file));
            if (StringUtils.isNotBlank(name)) {
                rb.header("Content-Disposition", "attachment; filename=" + name);
            }
            return rb.build();
        }
        throw new NotFoundException();
    }


    @GET
    @Path("/{bucket}/{fileId}/meta")
    @Produces("application/json")
    public Response meta(@PathParam("bucket") String bucket, @PathParam("fileId") String fileId) {
        throw new NotFoundException();
    }

    @PUT
    @Path("/{bucket}/{fileId}")
    @Produces("text/plain")
    public String write(@HeaderParam("Content-Type") String contentType, @PathParam("bucket") String bucket, @PathParam("fileId") String fileId, InputStream fileContent) {
        File file = file(bucket, fileId);
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(fileContent, out);
        } catch (IOException e) {
            logger.warn("Unable to write file [{}] [{}]", bucket, fileId, e);
            throw new WebApplicationException(507);
        }
        return fileId;
    }

    @PUT
    @Path("/{bucket}")
    @Produces("text/plain")
    public String write(@HeaderParam("Content-Type") String contentType, @PathParam("bucket") String bucket, InputStream fileContent) {
        return write(contentType, bucket, UUID.randomUUID().toString(), fileContent);
    }

    private File file(String bucket, String fileId) {
        File base = new File(basedir);
        base = new File(base, bucket);
        File file = base;
        long hash = fileId.hashCode();
        for (int i = 0; i < fanout; i++) {
            file = new File(file, "" + (hash % fanout));
            hash = hash / 10;
        }
        file.mkdirs();
        return new File(file, fileId);
    }
}
