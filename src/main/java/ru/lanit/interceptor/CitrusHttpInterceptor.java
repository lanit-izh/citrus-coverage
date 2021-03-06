package ru.lanit.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import ru.lanit.interfaces.CoverageOutputWriter;
import ru.lanit.utils.FileSystemOutputWriter;
import ru.lanit.utils.InterceptorHandler;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Response;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.BodyParameter;
import v2.io.swagger.models.parameters.PathParameter;
import v2.io.swagger.models.parameters.QueryParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static v2.io.swagger.models.Scheme.forValue;

public class CitrusHttpInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        URI uri = httpRequest.getURI();
        HttpHeaders headers = httpRequest.getHeaders();
        URL url = uri.toURL();
        InterceptorHandler interceptorHandler = new InterceptorHandler();
        String changedPath = null;
        Operation operation = new Operation();
        if (Objects.nonNull(interceptorHandler.getPathParams(headers))) {
            String decodeFullPath = interceptorHandler.changePathParam(interceptorHandler.getValueField(uri, "string", String.class),
                    httpRequest.getHeaders());
            changedPath = interceptorHandler.changePathParam(uri.getPath(), httpRequest.getHeaders());
            interceptorHandler.setUriPath(uri, changedPath);
            interceptorHandler.setValueObjectField(uri, decodeFullPath, "string");
            interceptorHandler.setValueObjectField(headers, decodeFullPath, "headers");

        }

        interceptorHandler.getQueryParams(uri).entrySet().stream().forEach(x -> operation
                .addParameter(new QueryParameter().name(x.getKey()).example(x.getValue())));

        interceptorHandler.getPathParams(headers).entrySet().stream()
                .forEach(x -> operation.addParameter(new PathParameter().name(x.getKey()
                        .replaceAll("[\\{\\}]", "")).example(x.getValue())));

        interceptorHandler.getHeadersParam(httpRequest.getHeaders()).stream().forEach(x -> operation.addParameter(x));

        interceptorHandler.getXWWWFormUrlEncoded(headers, bytes).stream().forEach(x -> operation.addParameter(x));

        interceptorHandler.getMultiPartParams(headers, bytes).stream().forEach(x -> operation.addParameter(x));

        ClientHttpResponse clientHttpResponse = clientHttpRequestExecution.execute(httpRequest, bytes);

        operation.addResponse(String.valueOf(clientHttpResponse.getStatusCode().value()), new Response());

        BufferedReader br = new BufferedReader(new InputStreamReader(clientHttpResponse.getBody()));
        String result = br.lines().collect(Collectors.joining("\n"));

        if (Objects.nonNull(clientHttpResponse.getBody())) {
            operation.addParameter(new BodyParameter().name("body"));
        }

        Swagger swagger = new Swagger()
                .scheme(forValue(uri.getScheme()))
                .host(uri.getHost())
                .consumes(String.valueOf(httpRequest.getHeaders().getContentType()))
                .produces(String.valueOf(clientHttpResponse.getHeaders().getContentType()))
                .path(InterceptorHandler.userPath, new Path().set(httpRequest.getMethod().name()
                        .toLowerCase(), operation));

        CoverageOutputWriter writer = new FileSystemOutputWriter(Paths.get("swagger-coverage-output-citrus"));
        writer.write(swagger);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}

