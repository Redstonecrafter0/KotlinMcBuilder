package net.redstonecraft.kotlinmcbuilder.handlers;

import net.redstonecraft.kotlinmcbuilder.Handlers;
import net.redstonecraft.kotlinmcbuilder.Main;
import net.redstonecraft.kotlinmcbuilder.Utils;
import net.redstonecraft.redstoneapi.core.enums.MimeType;
import net.redstonecraft.redstoneapi.core.http.HttpHeader;
import net.redstonecraft.redstoneapi.core.http.HttpResponseCode;
import net.redstonecraft.redstoneapi.core.tools.Hashlib;
import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.annotations.Route;
import net.redstonecraft.redstoneapi.webserver.annotations.RouteParam;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MavenRequestHandler extends RequestHandler {

    @Route("/maven/net/redstonecraft/kotlin-mc/<version>/<file>")
    public WebResponse request(WebRequest request, @RouteParam("version") String version, @RouteParam("file") String file) {
        return Handlers.handler(version, file);
    }

    @Route("/maven/net/redstonecraft/kotlin-mc/maven-metadata.xml")
    public WebResponse mavenMetadata(WebRequest request) {
        return new WebResponse(new ByteArrayInputStream(Handlers.manifest().getBytes(StandardCharsets.UTF_8)), HttpResponseCode.OK, new HttpHeader("Content-Type", MimeType.XML.getMimetype()));
    }

    @Route("/maven/net/redstonecraft/kotlin-mc/maven-metadata.xml.md5")
    public Object mavenMetadataMd5(WebRequest request) {
        return Hashlib.md5(Handlers.manifest());
    }

    @Route("/maven/net/redstonecraft/kotlin-mc/maven-metadata.xml.sha1")
    public Object mavenMetadataSha1(WebRequest request) {
        return Hashlib.sha1(Handlers.manifest());
    }

    @Route("/maven/net/redstonecraft/kotlin-mc/maven-metadata.xml.sha256")
    public Object mavenMetadataSha256(WebRequest request) {
        return Hashlib.sha256(Handlers.manifest());
    }

    @Route("/maven/net/redstonecraft/kotlin-mc/maven-metadata.xml.sha512")
    public Object mavenMetadataSha512(WebRequest request) {
        return Hashlib.sha512(Handlers.manifest());
    }

    @Route("/download/<version>")
    public WebResponse download(WebRequest request, @RouteParam("version") String version) {
        return Handlers.handler(version, "kotlinmc-" + version + ".jar");
    }

    @Route("/")
    public WebResponse home(WebRequest request) {
        try {
            Map<String, Object> map = new HashMap<>();
            List<String> versions = Utils.INSTANCE.getAvailableVersions();
            versions.sort(String::compareTo);
            Collections.reverse(versions);
            map.put("versions", versions);
            return WebResponse.create().setContent(
                    Main.webserver.getJinjava().render(
                            new String(Objects.requireNonNull(getClass().getResourceAsStream("/home.html")).readAllBytes(), StandardCharsets.UTF_8),
                            map)
            ).build();
        } catch (IOException e) {
            return Main.INSTANCE.getErrorHandler().handleError(HttpResponseCode.INTERNAL_SERVER_ERROR, "", new HashMap<>(), new HttpHeaders(new ArrayList<>()));
        }
    }

    @Route("/style.css")
    public InputStream styles(WebRequest request) {
        return getClass().getResourceAsStream("/style.css");
    }

}
