package io.openinnovationlabs.sales.adapters.http;

import io.openinnovationlabs.ddd.DomainModel;
import io.openinnovationlabs.ddd.Event;
import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;
import org.jboss.logging.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


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
    public Response createOpportunity(CreateOpportunity command) {
        LOGGER.info(command.toString());
        return Response.created(URI.create("1")).build();
    }


}