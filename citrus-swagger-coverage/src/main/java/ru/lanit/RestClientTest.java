package ru.lanit;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.testng.TestNGCitrusTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class RestClientTest extends TestNGCitrusTest {

    @Test
    @Parameters("testRunner")
    @CitrusTest(name = "test")
    public void test(@Optional @CitrusResource TestRunner testRunner) {
        testRunner.http(action -> {
            action.client("restClient")
                    .send()
                    .get("users?page=2")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .build();
        });
        testRunner.http(action -> {
            action.client("restClient")
                    .receive().response(HttpStatus.OK).contentType("application/json; charset=utf-8");
        });
    }

    @Test
    @Parameters("testRunner")
    @CitrusTest(name = "test2")
    public void petStoreTest(@Optional @CitrusResource TestRunner testRunner) {
        testRunner.http(action -> {
            action.client("restPetstore")
                    .send()
                    .get("petstore/v2/pet/1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .build();
        });
        testRunner.http(action -> {
            action.client("restPetstore")
                    .receive().response(HttpStatus.OK).contentType("application/json");
        });
    }
}
