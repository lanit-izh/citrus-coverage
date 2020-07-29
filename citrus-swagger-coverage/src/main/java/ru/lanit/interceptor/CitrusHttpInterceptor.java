package ru.lanit.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.testng.util.Strings;
import ru.lanit.utils.CoverageOutputWriter;
import ru.lanit.utils.FileSystemOutputWriter;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Response;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.BodyParameter;
import v2.io.swagger.models.parameters.HeaderParameter;
import v2.io.swagger.models.parameters.PathParameter;
import v2.io.swagger.models.parameters.QueryParameter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static v2.io.swagger.models.Scheme.forValue;

public class CitrusHttpInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        Map<String, List<String>> parameters = null;
        Operation operation = new Operation();
        operation.addParameter(new PathParameter().name(httpRequest.getURI().getPath()).example(httpRequest.getURI().getPath()));
        httpRequest.getHeaders().forEach((k, v) -> {
            operation.addParameter(new HeaderParameter().name(k).example(v.get(0)));
        });

        if (Strings.isNotNullAndNotEmpty(httpRequest.getURI().getQuery())) {
            parameters = Arrays.stream(httpRequest.getURI().getQuery().split("&"))
                    .map(this::splitQueryParameter)
                    .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey,
                            LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
        }

        parameters.forEach((n, v) -> operation.addParameter(new QueryParameter().name(n).example(v.get(0))));


        ClientHttpResponse clientHttpResponse = clientHttpRequestExecution.execute(httpRequest, bytes);

        operation.addResponse(String.valueOf(clientHttpResponse.getStatusCode().value()), new Response());
        if (Objects.nonNull(clientHttpResponse.getBody())) {
            operation.addParameter(new BodyParameter().name("BODY_PARAM_NAME"));
        }

        clientHttpResponse.getHeaders();
        Swagger swagger = new Swagger()
                .scheme(forValue(httpRequest.getURI().getScheme()))
                .host(httpRequest.getURI().getHost())
                .consumes(String.valueOf(httpRequest.getHeaders().getContentType()))
                .produces(String.valueOf(clientHttpResponse.getHeaders().getContentType()))
                .path(httpRequest.getURI().getPath(), new Path().set(httpRequest.getMethod().name().toLowerCase(), operation));

        httpRequest.getMethodValue();
        CoverageOutputWriter writer = new FileSystemOutputWriter(Paths.get("D:\\swagger"));
        writer.write(swagger);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    public AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        try {
            return new AbstractMap.SimpleImmutableEntry<>(
                    URLDecoder.decode(key, "UTF-8"),
                    URLDecoder.decode(value, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
