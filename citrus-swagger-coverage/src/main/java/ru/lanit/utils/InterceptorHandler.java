package ru.lanit.utils;

import org.springframework.http.HttpHeaders;

import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriTemplate;
import ru.lanit.interfaces.HttpCitrusSpecHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

public class InterceptorHandler implements HttpCitrusSpecHandler {

    @Override
    public String[] getPathParams(String path, HttpHeaders[] headers) {
        return new String[0];
    }

    @Override
    public String changePathParam(String path, HttpHeaders headers) {

        Map<String, String> parameters = headers.entrySet().stream().filter(x -> x.getKey().startsWith("{"))
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().get(0)));

        return path;
    }
}
