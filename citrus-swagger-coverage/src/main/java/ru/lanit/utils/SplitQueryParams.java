package ru.lanit.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.AbstractMap;

public interface SplitQueryParams {

    default AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String path) {
        final int idx = path.indexOf("=");
        final String key = idx > 0 ? path.substring(0, idx) : path;
        final String value = idx > 0 && path.length() > idx + 1 ? path.substring(idx + 1) : null;

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
