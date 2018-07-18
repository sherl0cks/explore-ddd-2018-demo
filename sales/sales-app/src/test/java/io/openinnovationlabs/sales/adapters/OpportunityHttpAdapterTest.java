package io.openinnovationlabs.sales.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openinnovationlabs.ddd.CommandProcessingResponse;
import io.openinnovationlabs.sales.ObjectMother;
import io.openinnovationlabs.sales.adapters.http.OpportunityDTO;
import io.openinnovationlabs.sales.domain.opportunity.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

        CommandProcessingResponse r = response.body().as(CommandProcessingResponse.class);
        Assert.assertEquals(r.events.size(), 1);
        Assert.assertTrue(r.events.get(0) instanceof OpportunityCreated);

        WinOpportunity winOpportunity = new WinOpportunity(response.header("Location"));
        Response response2 = given().body(winOpportunity).contentType("application/json").
                when().post("/opportunities/" + response.header("Location") + "/commands")
                .then()
                .statusCode(200)
                .extract().response();

        CommandProcessingResponse r2 = response2.body().as(CommandProcessingResponse.class);
        Assert.assertEquals(r2.events.size(), 1);
        Assert.assertTrue(r2.events.get(0) instanceof OpportunityWon);


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
