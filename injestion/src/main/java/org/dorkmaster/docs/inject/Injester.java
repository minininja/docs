package org.dorkmaster.docs.inject;

import com.google.common.collect.ImmutableSet;
import org.apache.camel.Exchange;
import org.apache.camel.Main;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.ExpressionAdapter;
import org.dorkmaster.docs.inject.util.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Injester {
    private static final String CONFIG = "config";
    private static Logger logger = LoggerFactory.getLogger(Injester.class);
    private static final Set<String> IGNORED_HEADERS = ImmutableSet.of(
            "From",
            "org.restlet.http.headers",
            "Content-Type",
            "CamelHttpResponseCode",
            "CamelRestletResponse"
    );

    private static void usage() {
        System.out.println("Usage: server <config.properties>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            if ("server".equals(args[0])) {
                if (args.length == 2) {
                    new Injester().run(args[1]);
                } else {
                    usage();
                }
            }
        } else {
            usage();
        }
    }

    public void run(String config) {
        Config.instance().load(CONFIG, config);

        try {
            Main main = new Main();
            main.addRouteBuilder(new ScannerRoute());
            main.addRouteBuilder(new FileUploadRoute());
            main.addRouteBuilder(new IndexRoute());
            main.run();
        } catch (Exception e) {
            logger.warn("Injester died unexpectedly", e);
        }
    }

    public class ScannerRoute extends RouteBuilder {
        private String protocol = Config.instance().value(CONFIG, "imap.protocol").asString("imaps");
        private String server = Config.instance().value(CONFIG, "imap.server").asString("imap.gmail.com");
        private String username = Config.instance().value(CONFIG, "imap.username").asString();
        private String password = Config.instance().value(CONFIG, "imap.password").asString();
        private boolean peek = Config.instance().value(CONFIG, "imap.polling.peek").asBool(true);
        private String searchTerm = Config.instance().value(CONFIG, "imap.searchterm").asString();
        private boolean delete = Config.instance().value(CONFIG, "imap.polling.delete").asBool(false);
        private boolean unseen = Config.instance().value(CONFIG, "imap.polling.unseen").asBool(false);
        private int delay = Config.instance().value(CONFIG, "imap.polling.delay").asInt(15 * 60);
        private boolean close = Config.instance().value(CONFIG, "imap.polling.closefolder").asBool(false);
        private boolean disconnect = Config.instance().value(CONFIG, "imap.polling.disconnect").asBool(true);

        @Override
        public void configure() throws Exception {
            String url = new UrlBuilder(protocol + "://" + server)
                    .param("username", username)
                    .param("password", password)
                    .param("peek", peek)
                    .param("searchTerm.body", searchTerm)
                    .param("delete", delete)
                    .param("unseen", unseen)
                    .param("consumer.delay", delay * 1000) // poll every 15 minutes by default
                    .param("closeFolder", close)
                    .param("disconnect", disconnect)
                    .build();

            from(url)
                    .process(new EmailHeaderProcessor())
                    .split(new SplitAttachmentsExpression())
                    .to("direct:attachmentConverter");

            from("direct:attachmentConverter")
                    .process(new AttachmentProcessor())
                    .to("direct:uploader");
        }
    }

    public class FileUploadRoute extends RouteBuilder {
        private String host = Config.instance().value(CONFIG, "storage.host").asString("localhost");
        private int port = Config.instance().value(CONFIG, "storage.port").asInt(9092);
        private String bucketPrefix = Config.instance().value(CONFIG, "storage.bucket.prefix").asString("prefix");
        private String bucketPattern = Config.instance().value(CONFIG, "storage.bucket.pattern").asString("yyyyMMdd");
        private String contentType = Config.instance().value(CONFIG, "storage.contentType").asString("application/octet-stream");
        private String path = Config.instance().value(CONFIG, "storage.path").asString("/v1/storage/{bucket}");
        private String protocol = Config.instance().value(CONFIG, "storage.protocol").asString("http");
        private String method = Config.instance().value(CONFIG, "storage.method").asString("put");

        @Override
        public void configure() throws Exception {
            from("direct:uploader")
                    .setHeader("bucket", simple(bucketPrefix + "-" + new SimpleDateFormat(bucketPattern).format(new Date())))
                    .setHeader("Content-Type", simple(contentType))
                    .to("log:Sending file to storage")
                    .to("restlet:" + protocol + "://" + host + ":" + port + path + "?restletMethod=" + method)
                    .to("direct:indexer");
        }
    }

    public class IndexRoute extends RouteBuilder {
        private String protocol = Config.instance().value(CONFIG, "index.protocol").asString("http");
        private String host = Config.instance().value(CONFIG, "index.host").asString("localhost");
        private int port = Config.instance().value(CONFIG, "index.port").asInt(9090);
        private String method = Config.instance().value(CONFIG, "index.method").asString("put");
        private String path = Config.instance().value(CONFIG, "index.path").asString("/v1/document");
        private String index = Config.instance().value(CONFIG, "index.index").asString("mfc");
        private String datePattern = Config.instance().value(CONFIG, "index.date.pattern").asString("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        private String unindexedTag = Config.instance().value(CONFIG, "index.tag.unindexed").asString("unindexed");
        private Collection<String> extraTags = Collections.emptyList();
        private String createdField = Config.instance().value(CONFIG, "index.field.created").asString("created");
        private String filenameField = Config.instance().value(CONFIG, "index.field.filename").asString("filename");

        public IndexRoute() {
            if (!Config.instance().value(CONFIG, "index.tag.extra").isNull()) {
                extraTags = Arrays.asList(Config.instance().value(CONFIG, "index.tag.extra").asString().split(","));
            }
        }

        @Override
        public void configure() throws Exception {
            from("direct:indexer")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            logger.info("Generating document for file");

                            JSONObject fields = new JSONObject();
                            fields.put(createdField, new SimpleDateFormat(datePattern).format(new Date()));
                            fields.put(filenameField, exchange.getIn().getHeader("filename"));
                            for (Map.Entry<String, Object> header : exchange.getIn().getHeaders().entrySet()) {
                                if (!IGNORED_HEADERS.contains(header.getKey())) {
                                    fields.put(
                                            header.getKey(),
                                            header.getValue().toString().replaceAll("_nl_", "\n").trim()
                                    );
                                }
                            }
                            String doc = new JSONObject()
                                    .put("index", index)
                                    .put("bucket", exchange.getIn().getHeader("bucket"))
                                    .put("fileId", exchange.getIn().getBody())
                                    .put("tags", new JSONArray(extraTags).put(unindexedTag))
                                    .put("fields", fields)
                                    .toString();
                            exchange.getIn().setBody(doc);
                        }
                    })
                    .setHeader("Content-Type", simple("application/json"))
                    .to("restlet:" + protocol + "://" + host + ":" + port + path + "?restletMethod=" + method);
        }
    }

    public class EmailHeaderProcessor implements Processor {
        private final String PREFIX = Config.instance().value(CONFIG, "email.header.prefix").asString("X-email-");
        private final Set<String> SKIPPED = ImmutableSet.of("breadcrumbId");

        @Override
        public void process(Exchange exchange) throws Exception {
            logger.info("Adding prefix to email headers");
            Map<String, Object> replacementHeaders = new HashMap<>();
            for (Map.Entry<String, Object> hdr : exchange.getIn().getHeaders().entrySet()) {
                String value = hdr.getValue().toString();
                value = value.replaceAll("\n", "_nl_").trim();
                value = value.replaceAll("\r", "_nl_").trim();
                if (SKIPPED.contains(hdr.getKey())) {
                    replacementHeaders.put(hdr.getKey(), value);
                } else {
                    replacementHeaders.put(PREFIX + hdr.getKey(), value);
                }
            }
            exchange.getIn().setHeaders(replacementHeaders);
        }
    }

    public class AttachmentProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            logger.info("Converting attachment to file");

            // there should be only one at this point
            Map.Entry<String, DataHandler> attachment = exchange.getIn().getAttachments().entrySet().iterator().next();
            try (InputStream in = attachment.getValue().getInputStream()) {
                exchange.getIn().getHeaders().put("originalFilename", attachment.getKey());
                exchange.getIn().setBody(in);
            }
        }
    }

    public class SplitAttachmentsExpression extends ExpressionAdapter {
        @Override
        public Object evaluate(Exchange exchange) {
            logger.info("Splitting attachments");

            // must use getAttachments to ensure attachments is initial populated
            if (exchange.getIn().getAttachments().isEmpty()) {
                return null;
            }

            // we want to provide a list of messages with 1 attachment per mail
            List<Message> answer = new ArrayList<>();

            for (Map.Entry<String, DataHandler> entry : exchange.getIn().getAttachments().entrySet()) {
                final Message copy = exchange.getIn().copy();
                copy.getAttachments().clear();
                copy.getAttachments().put(entry.getKey(), entry.getValue());
                answer.add(copy);
            }

            return answer;
        }
    }

    public class UrlBuilder {
        private String base;
        private Map<String, String> params = new HashMap<>();

        public UrlBuilder(String base) {
            this.base = base;
        }

        public UrlBuilder param(String key, Object value) {
            params.put(key, value.toString());
            return this;
        }

        public String build() {
            StringBuilder sb = new StringBuilder(base);
            if (params.size() > 0) {
                sb.append("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                // remove trailing &
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        }
    }
}
