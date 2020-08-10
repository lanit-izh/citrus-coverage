package ru.lanit;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.testng.TestNGCitrusTest;
import com.consol.citrus.http.message.HttpMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.lanit.annotations.RequestInterceptor;
import ru.lanit.utils.InterceptorHandler;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClientTest extends TestNGCitrusTest {
    private Object InterceptingClientHttpRequestFactory;

//    @Test
//    @Parameters("testRunner")
//    @CitrusTest(name = "test")
//    public void test(@Optional @CitrusResource TestRunner testRunner) {
//        testRunner.http(action -> {
//            action.client("restClient")
//                    .send()
//                    .get("users?page=2")
//                    .contentType(MediaType.APPLICATION_JSON_VALUE)
//                    .build();
//        });
//        testRunner.http(action -> {
//            action.client("restClient")
//                    .receive().response(HttpStatus.OK).contentType("application/json; charset=utf-8");
//        });
//    }

//    @Test
//    @Parameters("testRunner")
//    @CitrusTest(name = "test2")
//    public void petStoreTest(@Optional @CitrusResource TestRunner testRunner) {
//        testRunner.http(action -> {
//            action.client("restPetstore")
//                    .send()
//                    .get("petstore/v2/pet/findByStatus")
//                    .queryParam("status", "pending")
//                    .contentType(MediaType.APPLICATION_JSON_VALUE)
//                    .build();
//        });
//        testRunner.http(action -> {
//            action.client("restPetstore")
//                    .receive().response(HttpStatus.OK).contentType("application/json");
//        });
//    }

    @Test
    @Parameters("testRunner")
    @CitrusTest(name = "test3")
    public void petStorePathParamTest(@Optional @CitrusResource TestRunner testRunner) throws IOException, ScriptException {
        Map<String, Object> pathParams = new HashMap<>();
        List<String> list = new ArrayList<>();
        Map<String, Object> header = new HashMap<>();
        ScriptEngineManager manager = new ScriptEngineManager();
        MultiValueMap<String, Object> multiPartParams = new LinkedMultiValueMap<>();
        pathParams.put("{petId}", "3");

        HttpMessage httpMessage = new HttpMessage();
        httpMessage.getContextPath();
        testRunner.http(action -> {
            action.client("httpClient")
                    .send()
                    .get(InterceptorHandler.getPath("/pet/{petId}"))
                    .queryParam("queryPparam","123")
                    .queryParam("query","123")
                    .queryParam("hello","234")

                    .headers(pathParams)
                    .build();
        });
    }

    //        testRunner.http(action -> {
//        action.client("httpClient")
//
//                .send()
//                .get(URIUtil.encodePath("pet/{petId}"))
//
//                .messageType(MessageType.JSON)
//
//                .post(URIUtil.encodePath("/petstore/v2/object?id=5"))
//                .message(new HttpMessage(multiPartParams))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .get(URIUtil.encodePath("pet/{petId}"))
//
//                .messageType(MessageType.JSON)
//                .headers(pathParams)
//                .build();
//    });
    @RequestInterceptor
    public void show() {
    }
}
