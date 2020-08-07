package ru.lanit.interfaces;

import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface HttpCitrusSpecHandler extends SplitQueryParams {

    Map<String, String> getPathParams( HttpHeaders headers);

    URI setUriPath(URI uri, String path);

    String changePathParam(String path, HttpHeaders headers);

    Map<String, List<String>>  splitParams(String path);
}
