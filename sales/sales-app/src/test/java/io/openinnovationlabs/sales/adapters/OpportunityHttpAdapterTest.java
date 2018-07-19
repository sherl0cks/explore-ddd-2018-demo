package io.openinnovationlabs.sales.adapters;

import com.atlassian.oai.validator.restassured.SwaggerValidationFilter;
import io.openinnovationlabs.ddd.CommandProcessingResponse;
import io.openinnovationlabs.sales.ObjectMother;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityWon;
import io.openinnovationlabs.sales.dto.CommandProcessingResponseDTO;
import io.openinnovationlabs.sales.dto.CreateOpportunityCommandDTO;
import io.openinnovationlabs.sales.dto.OpportunityCreatedEventDTO;
import io.openinnovationlabs.sales.dto.WinOpportunityCommandDTO;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
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

    private final SwaggerValidationFilter validationFilter = new SwaggerValidationFilter("sales-open-api-spec.yaml");
    @Value("${local.server.port}")
    private int port;

    @Before
    public void beforeTest() {
        RestAssured.baseURI = String.format("http://localhost:%d/api/v1", port);

    }

    // TODO re-enable filter https://bitbucket.org/atlassian/swagger-request-validator/issues/140/unexpected-failure-on-openapi-3-request
    @Test
    public void shouldCreateOpportunityAndWinOpportunity() {
        CreateOpportunityCommandDTO createOpportunityCommandDTO = ObjectMother.createOpportunityCommandDTO();
        Response response =
                given()
                        .body(createOpportunityCommandDTO).contentType("application/json")
                        .log().body()
                        // .filter(validationFilter)
                        .when()
                        .post("/opportunities")
                        .then().assertThat()
                        .header("Location", notNullValue())
                        .header("Location", not(isEmptyString()))
                        .statusCode(201)
                        .extract().response();

        CommandProcessingResponseDTO r = response.body().as(CommandProcessingResponseDTO.class);
        Assert.assertEquals(r.getEvents().size(), 1);
        Assert.assertTrue(r.getEvents().get(0) instanceof OpportunityCreatedEventDTO);

        WinOpportunityCommandDTO winOpportunityCommandDTO = ObjectMother.winOpportunityCommandDTO();
        Response response2 =
                given()
                        .body(winOpportunityCommandDTO).contentType("application/json")
                        .log().body()
                        // .filter(validationFilter)
                        .when()
                        .post("/opportunities/" + response.header("Location"))
                        .then()
                        .assertThat().statusCode(200)
                        .extract().response();

        CommandProcessingResponse r2 = response2.body().as(CommandProcessingResponse.class);
        Assert.assertEquals(r2.events.size(), 1);
        Assert.assertTrue(r2.events.get(0) instanceof OpportunityWon);


    }

    @Test
    public void shouldFailToWinOpportunity() {

        String id = UUID.randomUUID().toString();
        WinOpportunityCommandDTO winOpportunityCommandDTO = ObjectMother.winOpportunityCommandDTO();
        given().body(winOpportunityCommandDTO).contentType("application/json").
                when().post("/opportunities/" + id)
                .then()
                .statusCode(400);
    }
}
