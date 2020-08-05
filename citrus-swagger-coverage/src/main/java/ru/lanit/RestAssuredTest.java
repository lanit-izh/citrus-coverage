package ru.lanit;

import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.testng.annotations.Test;

@Test
public class RestAssuredTest {

//    @Test(enabled = false)
//    public void sendRequest1Test() {
//        RestAssured.given().filter( new SwaggerCoverageRestAssured())
//                .contentType(ContentType.JSON)
//                .when()
//                .get("http://localhost:8080/petstore/v2/pet/findByStatus&status=available")
//                .then()
//                .log().all()
//                .extract()
//                .response();
//    }
//
//    @Test
//    public void sendRequest2Test() {
//        RestAssured.given().filter( new SwaggerCoverageRestAssured())
//                .contentType(ContentType.JSON)
//                .when()
//                .get("http://localhost:8080/petstore/v2/pet/findByStatus&status=available")
//                .then()
//                .log().all()
//                .extract()
//                .response();
//    }

    public void shouldCatchExceptionRestAssuredIssue1232() {
        RestAssured.given().filter(new SwaggerCoverageRestAssured())
                .multiPart("file", "{}")
                .header(new Header("X-Request-ID", "h"))
                .formParam("form_param", "f", "f2")
                .queryParam("query_param", "q", "q2")
                .pathParam("path_param", "p")
                .get("/hello/{path_param}");
    }
}
