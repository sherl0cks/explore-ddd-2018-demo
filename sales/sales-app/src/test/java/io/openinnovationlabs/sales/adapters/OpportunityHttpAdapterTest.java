package io.openinnovationlabs.sales.adapters;

import io.openinnovationlabs.sales.ObjectMother;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpportunityHttpAdapterTest {

    @Value("${local.server.port}")
    private int port;


    @Before
    public void beforeTest() {
        RestAssured.baseURI = String.format("http://localhost:%d/api", port);

    }

    @Test
    public void shouldCreateOpportunity() {
        given().body(ObjectMother.createOpportunityCommand()).contentType("application/json")
                .when().post("/opportunities")
                .then()
                .statusCode(201);
    }
}
