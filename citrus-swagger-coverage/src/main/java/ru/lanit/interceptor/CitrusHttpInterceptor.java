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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static v2.io.swagger.models.Scheme.forValue;

public class CitrusHttpInterceptor implements ClientHttpRequestInterceptor {

    private Object paramToRemovePathElementsFromStartOfPath;

    public CitrusHttpInterceptor() {
    }

    public CitrusHttpInterceptor(Object paramToRemovePathElementsFromStartOfPath) {
        this.paramToRemovePathElementsFromStartOfPath = paramToRemovePathElementsFromStartOfPath;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        URI uri = httpRequest.getURI();
        HttpHeaders headers = httpRequest.getHeaders();
        InterceptorHandler interceptorHandler = new InterceptorHandler();
        Map<String, String> pathParams = new HashMap<>();
        Operation operation = new Operation();
        if (Objects.nonNull(interceptorHandler.getPathParams(headers))) {
            String decodeFullPath = interceptorHandler.changePathParam(interceptorHandler.getValueField(uri, "string", String.class),
                    httpRequest.getHeaders());
            pathParams = interceptorHandler.getPathParams(headers);
            String changedPath = interceptorHandler.changePathParam(uri.getPath(), httpRequest.getHeaders());
            interceptorHandler.setUriPath(uri, changedPath);
            interceptorHandler.setValueObjectField(uri, changedPath, "decodedPath");
            interceptorHandler.setValueObjectField(uri, changedPath, "path");
            interceptorHandler.setValueObjectField(uri, decodeFullPath, "string");
            interceptorHandler.removePathParams(headers, pathParams);
        }

        interceptorHandler.getQueryParams(uri).entrySet().stream().forEach(x -> operation
                .addParameter(new QueryParameter().name(x.getKey()).example(x.getValue())));

        pathParams.entrySet().stream()
                .forEach(x -> operation.addParameter(new PathParameter().name(x.getKey()
                        .replaceAll("[\\{\\}]", "")).example(x.getValue())));

        interceptorHandler.getHeadersParam(httpRequest.getHeaders()).stream().forEach(x -> operation.addParameter(x));

        interceptorHandler.getXWWWFormUrlEncoded(headers, bytes).stream().forEach(x -> operation.addParameter(x));

        interceptorHandler.getMultiPartParams(headers, bytes).stream().forEach(x -> operation.addParameter(x));

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
                .path(getSwaggerPath(InterceptorHandler.userPath), new Path().set(httpRequest.getMethod().name()
                        .toLowerCase(), operation));

        CoverageOutputWriter writer = new FileSystemOutputWriter(Paths.get("swagger-coverage-output"));
        writer.write(swagger);
        return clientHttpResponse;
    }

    /**
     * Метод для получения корректного path для маппинга с swagger спецификацией
     * @param userPath - path сервиса, по которому отправляется запрос
     * @return возвращает скорректированный path для маппинга с swagger спецификацией
     */
    private String getSwaggerPath(String userPath) {

        StringBuilder pathToRemove = new StringBuilder();

        if (paramToRemovePathElementsFromStartOfPath != null) {
            boolean isStartWithSlash = userPath.startsWith("/");

            String[] pathParts = isStartWithSlash
                    ? userPath.replaceFirst("/", "").split("/")
                    : userPath.split("/");

            if (paramToRemovePathElementsFromStartOfPath instanceof String) {
                int elementIndexForRemoveElementsBefore = -1;
                for (int i = 0; i < pathParts.length; i++) {
                    if (pathParts[i].equals(paramToRemovePathElementsFromStartOfPath)) {
                        elementIndexForRemoveElementsBefore = i;
                    }
                }
                if (elementIndexForRemoveElementsBefore != -1) {
                    for (int i = 0; i < elementIndexForRemoveElementsBefore; i++) {
                        pathToRemove.append("/").append(pathParts[i]);
                    }
                }
            }

            if (paramToRemovePathElementsFromStartOfPath instanceof Integer) {
                for (int i = 0; i < (Integer) paramToRemovePathElementsFromStartOfPath; i++) {
                    pathToRemove.append("/").append(pathParts[i]);
                }
            }

            return isStartWithSlash
                    ? userPath.replaceFirst(pathToRemove.toString(), "")
                    : ("/" + userPath).replaceFirst(pathToRemove.toString(), "");
        } else {
            return userPath;
        }
    }
}

