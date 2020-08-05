package ru.lanit.interfaces;

import org.springframework.http.HttpHeaders;

public interface HttpCitrusSpecHandler {

    String[] getPathParams(String path, HttpHeaders[] headers);

    String changePathParam(String path, HttpHeaders headers);
}
