package ru.lanit.utils;

import org.springframework.http.HttpHeaders;
import ru.lanit.interfaces.HttpCitrusSpecHandler;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class InterceptorHandler implements HttpCitrusSpecHandler {

    @Override
    public Map<String, String> getPathParams(HttpHeaders headers) {
        return headers.entrySet().stream().filter(x -> x.getKey().startsWith("{") && x.getKey().endsWith("}"))
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().get(0)));
    }

    @Override
    public URI setUriPath(URI uri, String path) {
        try {
            Field pathField = uri.getClass().getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(uri, path);
            Field decodedPathFiled = uri.getClass().getDeclaredField("decodedPath");
            decodedPathFiled.setAccessible(true);
            decodedPathFiled.set(uri, path);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public String changePathParam(String path, HttpHeaders headers) {
        Map<String, String> parameters = getPathParams(headers);
        StringBuilder stringBuilder = new StringBuilder();
        String splitPath[] = path.replaceFirst("/", "").trim().split("/");

        for (String value : splitPath) {
            if (Objects.nonNull(parameters.get(value))) {
                stringBuilder.append("/" + parameters.get(value));
            } else {
                stringBuilder.append("/" + value);
            }
        }
        return stringBuilder.toString();
    }
}
