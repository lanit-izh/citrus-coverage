package ru.lanit.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import ru.lanit.utils.CoverageOutputWriter;
import ru.lanit.utils.FileSystemOutputWriter;
import ru.lanit.utils.InterceptorHandler;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Response;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.BodyParameter;
import v2.io.swagger.models.parameters.FormParameter;
import v2.io.swagger.models.parameters.HeaderParameter;
import v2.io.swagger.models.parameters.PathParameter;
import v2.io.swagger.models.parameters.QueryParameter;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static v2.io.swagger.models.Scheme.forValue;

public class CitrusHttpInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        URI uri = httpRequest.getURI();
        InterceptorHandler interceptorHandler = new InterceptorHandler();
        String beforeChangingPath = uri.getPath();
        String changedPath = null;
        Map<String, String> pathParameters = null;
        Operation operation = new Operation();

        if (Objects.nonNull(interceptorHandler.getPathParams(httpRequest.getHeaders()))) {
            pathParameters = interceptorHandler.getPathParams(httpRequest.getHeaders());
            changedPath = interceptorHandler.changePathParam(uri.getPath(), httpRequest.getHeaders());
            interceptorHandler.setUriPath(uri, changedPath);
        }
        pathParameters.entrySet().stream().forEach(x -> operation.addParameter(new PathParameter().name(x.getKey())
                .example(x.getValue())));

        operation.addParameter(new HeaderParameter().name("Content-Type").example(httpRequest.getHeaders()
                .getContentType().toString()));

        operation.addParameter(new PathParameter().name(uri.getPath()));

        if (httpRequest.getHeaders().getAccept().contains(MediaType.ALL)) {
            operation.addParameter(new HeaderParameter().name("Accept").example(MediaType.ALL_VALUE));
        } else {
            operation.addParameter(new HeaderParameter().name("Accept").example(httpRequest.getHeaders().getAccept()
                    .get(0).toString()));
        }
        interceptorHandler.getQueryParams(uri).forEach((n, v) -> operation.addParameter(new QueryParameter().name(n + "=" + v)));
        if (httpRequest.getHeaders().getContentType().getSubtype().equalsIgnoreCase("x-www-form-urlencoded")) {
            interceptorHandler.getFormParams(bytes).forEach((n, v) -> operation.addParameter(new FormParameter().name(n).example(v)));
        }
        if (httpRequest.getHeaders().getContentType().getType().equalsIgnoreCase("multipart")) {
            interceptorHandler.getMultiPartParamsNames(bytes).forEach(multiPartName -> operation.addParameter(new FormParameter().name(multiPartName)));
        }

        ClientHttpResponse clientHttpResponse = clientHttpRequestExecution.execute(httpRequest, bytes);
        operation.addResponse(String.valueOf(clientHttpResponse.getStatusCode().value()), new Response());
        if (Objects.nonNull(clientHttpResponse.getBody())) {
            operation.addParameter(new BodyParameter().name("body"));
        }

        Swagger swagger = new Swagger()
                .scheme(forValue(uri.getScheme()))
                .host(uri.getHost())
                .consumes(String.valueOf(httpRequest.getHeaders().getContentType()))
                .produces(String.valueOf(clientHttpResponse.getHeaders().getContentType()))
                .path(beforeChangingPath, new Path().set(httpRequest.getMethod().name()
                        .toLowerCase(), operation));

        CoverageOutputWriter writer = new FileSystemOutputWriter(Paths.get("swagger-coverage-output-citrus"));
        writer.write(swagger);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
