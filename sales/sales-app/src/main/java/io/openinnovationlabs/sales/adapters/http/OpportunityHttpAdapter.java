package io.openinnovationlabs.sales.adapters.http;

import io.openinnovationlabs.ddd.DomainModel;
import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;


@Path("/")
@Component
public class OpportunityHttpAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityHttpAdapter.class);

    @Autowired
    private DomainModel domainModel;

    @GET
    @Path("/greeting")
    @Produces("application/json")
    public String greeting(@QueryParam("name") @DefaultValue("World") String name) {

        domainModel.issueCommand(new CreateOpportunity(new OpportunityId("1"), "test", "residency"));
        return "hi";
    }

    @POST
    @Path("/opportunities")
    @Produces("application/json")
    @Consumes("application/json")
    public void createOpportunity(OpportunityDTO opportunityDTO,
                                      @Suspended final AsyncResponse asyncResponse) {

        CreateOpportunity createOpportunity = opportunityDTO.to(UUID.randomUUID().toString());
        domainModel.issueCommand( createOpportunity ).setHandler( ar -> {
            if (ar.succeeded()){
                asyncResponse.resume(Response.created(URI.create(createOpportunity.aggregateIdentity().id)).build());
            } else {
                asyncResponse.resume(Response.serverError());
            }
        });

    }


}