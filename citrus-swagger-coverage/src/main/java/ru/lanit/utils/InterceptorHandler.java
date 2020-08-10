package ru.lanit.utils;

import org.eclipse.jetty.util.URIUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testng.util.Strings;
import ru.lanit.interfaces.HttpCitrusSpecHandler;
import v2.io.swagger.models.parameters.HeaderParameter;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InterceptorHandler implements HttpCitrusSpecHandler {

    public static String userPath;

    public static String getUserPath(String path) {
        userPath = path;
        return URIUtil.encodePath(path);
    }

    public List<HeaderParameter> getHeadersParam(HttpHeaders headers) {
        List<HeaderParameter> headerParameters = new ArrayList<>();
        headers.getAccept();
        HeaderParameter headerParameter = new HeaderParameter();
        headers.entrySet().stream().filter((h -> !(h.getKey().startsWith("{") && h.getKey()
                .endsWith("}")))).forEach((x) -> {
            if (x.getKey().equals("Accept") && Arrays.stream(x.getValue().get(0).split(",")).map(z -> z.trim())
                    .collect(Collectors.toList()).contains(MediaType.ALL_VALUE)) {
                headerParameters.add( new HeaderParameter().name("Accept").example(MediaType.ALL_VALUE));
            } else if (!(x.getKey().equals("Content-Length"))&&!(x.getKey().equals("multipart"))
                    &&!(x.getKey().equals("x-www-form-urlencoded"))) {
                headerParameters.add(new HeaderParameter().name(x.getKey()).example(x.getValue().toString()
                        .replaceAll("[\\[\\]]", "")));
            }
        });
        return headerParameters;
    }

    @Override
    public Map<String, String> getPathParams(HttpHeaders headers) {
        return headers.entrySet().stream().filter(x -> x.getKey().startsWith("{") && x.getKey().endsWith("}"))
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().get(0)));
    }

    @Override
    public URI setUriPath(URI uri, String path) {
        try {
            Field pathField = uri.getClass().getDeclaredField("path");
            Field decodedPathFiled = uri.getClass().getDeclaredField("decodedPath");
            pathField.setAccessible(true);
            decodedPathFiled.setAccessible(true);
            pathField.set(uri, path);
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
        String[] splitPath = path.replaceFirst("/", "").trim().split("/");

        for (String value : splitPath) {
            if (Objects.nonNull(parameters.get(value))) {
                stringBuilder.append("/" + parameters.get(value));
            } else {
                stringBuilder.append("/" + value);
            }
        }
        return stringBuilder.toString();
    }

    public Map<String, String> getQueryParams(URI uri) throws UnsupportedEncodingException {
        String[] buf;
        Map<String, String> res = new HashMap<>();
        if (Strings.isNotNullAndNotEmpty(uri.getQuery())) {
            for (String couple : uri.getQuery().split("&")) {
                buf = couple.split("=");
                res.put(URLDecoder.decode(buf[0], "UTF-8"),
                        buf.length > 1 ? URLDecoder.decode(buf[1], "UTF-8") : "");
            }
        }

        buf = uri.getPath().split("\\?");
        if (buf.length > 1 && buf[1].length() > 0) {
            for (String couple : buf[1].split("&")) {
                buf = couple.split("=");
                res.put(URLDecoder.decode(buf[0], "UTF-8"),
                        buf.length > 1 ? URLDecoder.decode(buf[1], "UTF-8") : "");
            }
        }

        return res;
    }

    public Map<String, String> getFormParams(byte[] body) {
        String[] buf;
        Map<String, String> res = new HashMap<>();
        if (body.length == 0) {
            return res;
        }
        String formParams = new String(body);
        for (String couple : formParams.split("&")) {
            buf = couple.split("=");
            res.put(buf[0], buf[1]);
        }
        return res;
    }

    public Collection<String> getMultiPartParamsNames(byte[] body) {
        Set<String> res = new HashSet<>();
        if (body.length == 0) {
            return res;
        }
        Pattern pattern = Pattern.compile("name=\"(?<paramName>.*?)\"");
        Matcher matcher = pattern.matcher(new String(body));
        while (matcher.find()) {
            res.add(matcher.group("paramName"));
        }
        return res;
    }

    public Map<String, List<String>> processingHeaders(HttpHeaders headers) {
        return null;
    }
}
