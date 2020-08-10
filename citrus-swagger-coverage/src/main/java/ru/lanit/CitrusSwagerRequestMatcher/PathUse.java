package ru.lanit.CitrusSwagerRequestMatcher;

import java.util.HashMap;
import java.util.Map;

public class PathUse {
    public final String PATH;
    public Map<String, String> params;

    PathUse(String path) {
        this.PATH = path;
        params = new HashMap<>();
    }

    void addParam(String paramName, String value){
        params.put(paramName, value);
    }

}
