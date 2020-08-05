package ru.lanit.utils;

import org.springframework.http.HttpHeaders;
import ru.lanit.interfaces.HttpCitrusSpecHandler;

import java.util.Arrays;

public class InterceptorHandler implements HttpCitrusSpecHandler {

    @Override
    public String[] getPathParams(String path, HttpHeaders[] headers) {
        return new String[0];
    }

    @Override
    public String changePathParam(String path, HttpHeaders headers) {
//        Arrays.stream(headers).filter(x->x.entrySet().)
        return null;
    }
}
