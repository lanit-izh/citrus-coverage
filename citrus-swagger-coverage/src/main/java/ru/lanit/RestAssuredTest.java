package ru.lanit;

import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

@Test
public class RestAssuredTest {

    @Test
    public void sendRequestTest() {
        RestAssured.given().filter( new SwaggerCoverageRestAssured())
                .contentType(ContentType.JSON)
                .when()
                .queryParam("page=2")
                .get("https://reqres.in/api/users")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
