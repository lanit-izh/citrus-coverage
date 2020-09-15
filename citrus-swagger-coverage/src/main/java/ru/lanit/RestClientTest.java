package ru.lanit;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.testng.TestNGCitrusTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.lanit.annotations.RequestInterceptor;
import ru.lanit.utils.InterceptorHandler;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.HashMap;
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

        MultiValueMap<String, Object> multiPartParams = new LinkedMultiValueMap<>();

        pathParams.put("{petId}", "3");

        Resource file = new ClassPathResource("data/jpg.png");
        multiPartParams.add("file", file);
        testRunner.http(action -> {
            action.client("httpClient")
                    .send()
                    .post(InterceptorHandler.getPath("/pet/1/uploadImage"))
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .payload(multiPartParams)
                    .build();
        });
    }
//        testRunner.http(action -> {
//            action.client("httpClient")
//                    .send()
//                    .get(InterceptorHandler.getPath("/pet/{petId}"))
//                    .contentType(MediaType.APPLICATION_JSON_VALUE)
//                    .headers(pathParams)
//                    .build();
//        });
    //   }

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
