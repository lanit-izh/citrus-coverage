package ru.lanit;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.testng.TestNGCitrusTest;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageType;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.lanit.aspects.RequestInterceptor;
import ru.lanit.citrus.Mesaga;

import java.util.HashMap;
import java.util.Map;


public class RestClientTest extends TestNGCitrusTest {

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
    public void petStorePathParamTest(@Optional @CitrusResource TestRunner testRunner) {
        Mesaga mesaga = new Mesaga();
        Map<String, Object> map = new HashMap<>();
        Map<String, String> pathParam = new HashMap<>();
        map.put("Petid", "{petId}");
        map.put("fdsfds", "{weer}");
        map.put("Pe2323tid", "{3333}");
        pathParam.put("Petid", "{petId}");
        pathParam.put("fdsfds", "{weer}");
        pathParam.put("Pe2323tid", "{3333}");
        mesaga.setPathParam(pathParam);

        Message message;
        testRunner.createVariable("pet", "1");


        testRunner.http(action -> {
            action.client("helloHttpClient")
                    .send()

                    .get("petstore/v2/2")
                    .messageType(MessageType.JSON)
                    .message(mesaga)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .headers(map)
                    .description("expert");

        });

    }

    @RequestInterceptor
    public void show(){

    }

}
