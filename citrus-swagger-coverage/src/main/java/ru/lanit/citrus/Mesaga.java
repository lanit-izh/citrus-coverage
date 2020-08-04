package ru.lanit.citrus;

import com.consol.citrus.message.DefaultMessage;

import java.util.HashMap;
import java.util.Map;

public class Mesaga extends DefaultMessage {
    Map<String,String> pathParam = new HashMap<>();

    public void setPathParam(Map<String, String> pathParam) {
        this.pathParam = pathParam;
    }
}
