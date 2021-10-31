package net.redstonecraft.kotlinmcbuilder.handlers;

import net.redstonecraft.redstoneapi.core.http.HttpResponseCode;
import net.redstonecraft.redstoneapi.webserver.ErrorHandler;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PlainErrorHandler extends ErrorHandler {

    @Override
    public WebResponse handleError(HttpResponseCode code, String url, Map<String, String> args, HttpHeaders headers) {
        return new WebResponse(new ByteArrayInputStream((code.getCode() + " " + code.getDescription()).getBytes(StandardCharsets.UTF_8)), code);
    }

}
