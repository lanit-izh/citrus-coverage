package ru.lanit.interfaces;

import org.springframework.http.HttpHeaders;
import v2.io.swagger.models.parameters.FormParameter;
import v2.io.swagger.models.parameters.HeaderParameter;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface HttpCitrusSpecHandler {

    Map<String, String> getPathParams(HttpHeaders headers);

    URL setUrlPath(URL url, String path);

    URL setUrlPath(URL url, String path, String nameField);

    URI setUriPath(URI uri, String path);

    String changePathParam(String path, HttpHeaders headers);

    List<HeaderParameter> getHeadersParam(HttpHeaders headers);

    <T, V> T getValueField(V object, String nameField, Class<T> clazz);

    List<FormParameter> getXWWWFormUrlEncoded(HttpHeaders headers, byte[] bytes);

    List<FormParameter> getMultiPartParams(HttpHeaders headers, byte[] bytes);

    Map<String, String> getFormParams(byte[] body);

    <T, V> T setValueObjectField(T object, V value, String nameField);
}
