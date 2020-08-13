package ru.lanit.utils;

import org.eclipse.jetty.util.URIUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testng.util.Strings;
import ru.lanit.interfaces.HttpCitrusSpecHandler;
import ru.lanit.interfaces.SplitQueryParams;
import v2.io.swagger.models.parameters.FormParameter;
import v2.io.swagger.models.parameters.HeaderParameter;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InterceptorHandler implements HttpCitrusSpecHandler, SplitQueryParams {

    public static String userPath;

    public static String getPath(String path) {
        userPath = path.replace("//", "/");
        return URIUtil.encodePath(userPath);
    }

    @Override
    public List<HeaderParameter> getHeadersParam(HttpHeaders headers) {
        List<HeaderParameter> headerParameters = new ArrayList<>();
        headers.entrySet().stream().filter((h -> !(h.getKey().startsWith("{") && h.getKey()
                .endsWith("}")))).forEach((x) -> {
            if (x.getKey().equals("Accept") && Arrays.stream(x.getValue().get(0).split(",")).map(z -> z.trim())
                    .collect(Collectors.toList()).contains(MediaType.ALL_VALUE)) {
                headerParameters.add(new HeaderParameter().name("Accept").example(MediaType.ALL_VALUE));
            } else if (!(x.getKey().equals("Content-Length")) && !(x.getKey().equals("multipart"))
                    && !(x.getKey().equals("x-www-form-urlencoded"))) {
                headerParameters.add(new HeaderParameter().name(x.getKey()).example(x.getValue().toString()
                        .replaceAll("[\\[\\]]", "")));
            }
        });
        return headerParameters;
    }

    public <T, V> T getValueField(V object, String nameField, Class<T> clazz) {

        T value = null;
        try {
            Field pathField = object.getClass().getDeclaredField(nameField);
            pathField.setAccessible(true);
            value = (T) pathField.get(object);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public List<FormParameter> getXWWWFormUrlEncoded(HttpHeaders headers, byte[] bytes) {
        List<FormParameter> list = new ArrayList<>();
        if (headers.getContentType().getSubtype().equalsIgnoreCase("x-www-form-urlencoded")) {
            getFormParams(bytes).forEach((n, v) -> list.add(new FormParameter().name(n).example(v)));
        }
        return list;
    }

    @Override
    public List<FormParameter> getMultiPartParams(HttpHeaders headers, byte[] bytes) {
        List<FormParameter> list = new ArrayList<>();
        if (headers.getContentType().getType().equalsIgnoreCase("multipart")) {
            getMultiPartParamsNames(bytes).forEach(n -> list.add(new FormParameter().name(n)));
        }
        return list;
    }

    @Override
    public Map<String, String> getPathParams(HttpHeaders headers) {
        return headers.entrySet().stream().filter(x -> x.getKey().startsWith("{") && x.getKey().endsWith("}"))
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().get(0)));
    }

    @Override
    public URL setUrlPath(URL url, String path) {
        try {
            Field pathField = url.getClass().getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(url, path);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public URL setUrlPath(URL url, String path, String nameField) {
        try {
            Field pathField = url.getClass().getDeclaredField(nameField);
            pathField.setAccessible(true);
            pathField.set(url, path);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return url;
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

    public <T, V> T setValueObjectField(T object, V value, String nameField) {
        try {
            Field pathField = object.getClass().getDeclaredField(nameField);
            pathField.setAccessible(true);
            pathField.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public String changePathParam(String path, HttpHeaders headers) {
        Map<String, String> parameters = getPathParams(headers);
        StringBuilder stringBuilder = new StringBuilder();
        String[] splitPath = URIUtil.decodePath(path).replaceFirst("/", "").trim().split("/");

        for (String value : splitPath) {
            if (Strings.isNotNullAndNotEmpty(parameters.get(value))) {
                stringBuilder.append("/" + parameters.get(value));

            } else if (!(value.equals("https:")) && !(value.equals("http:"))) {
                stringBuilder.append("/" + value);
            } else {
                stringBuilder.append(value + "/");
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

    @Override
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

    public void removePathParams(HttpHeaders headers, Map<String, String> pathParams) {
        pathParams.entrySet().stream().forEach(x -> {
            if (Objects.nonNull(headers.get(x.getKey()))) {
                headers.remove(x.getKey());
            }
        });
    }
}
