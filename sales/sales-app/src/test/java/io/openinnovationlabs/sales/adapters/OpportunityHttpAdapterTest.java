package io.openinnovationlabs.sales.adapters;

import io.openinnovationlabs.sales.ObjectMother;
import io.openinnovationlabs.sales.adapters.http.OpportunityDTO;
import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.WinOpportunity;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
    public void shouldCreateOpportunityAndWinOpportunity() {
        OpportunityDTO createOpportunity = ObjectMother.opportunityDTO();
        Response response = given().body(createOpportunity).contentType("application/json")
                .when().post("/opportunities/")
                .then()
                .header("Location", notNullValue())
                .header("Location", not(isEmptyString()))
                .statusCode(201).extract().response();

        WinOpportunity winOpportunity = new WinOpportunity(response.header("Location"));
        given().body(winOpportunity).contentType("application/json").
                when().post("/opportunities/" + response.header("Location") + "/commands")
                .then()
                .statusCode(200);
        //given().body( new WinOpportunity(c))
    }

    @Test
    public void shouldFailToWinOpportunity() {

        String id = UUID.randomUUID().toString();
        WinOpportunity winOpportunity = new WinOpportunity(id);
        given().body(winOpportunity).contentType("application/json").
                when().post("/opportunities/" + id + "/commands")
                .then()
                .statusCode(400);
    }
}
