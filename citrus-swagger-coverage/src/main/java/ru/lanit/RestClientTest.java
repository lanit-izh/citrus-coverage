package ru.lanit;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.testng.TestNGCitrusTest;
import com.consol.citrus.message.MessageType;
import org.eclipse.jetty.util.URIUtil;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.lanit.annotations.RequestInterceptor;

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
    public void petStorePathParamTest(@Optional @CitrusResource TestRunner testRunner) throws IOException {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", "1");
        testRunner.http(action -> {
            action.client("httpClient")
                    .send()
                    .get(URIUtil.encodePath("petstore/{v2}/{id}"))
                    .messageType(MessageType.JSON)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .headers(pathParams)
                    .description("expert")
                    .build();
        });
    }

    @RequestInterceptor
    public void show() {
    }
}
