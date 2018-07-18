package io.openinnovationlabs.sales.adapters.http;

import io.openinnovationlabs.ddd.Command;
import io.openinnovationlabs.ddd.DomainModel;
import io.openinnovationlabs.ddd.DomainModelException;
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


    @POST
    @Path("/opportunities")
    @Produces("application/json")
    @Consumes("application/json")
    public void createOpportunity(OpportunityDTO opportunityDTO,
                                  @Suspended final AsyncResponse asyncResponse) {

        CreateOpportunity createOpportunity = opportunityDTO.to(UUID.randomUUID().toString());
        domainModel.issueCommand(createOpportunity).setHandler(ar -> {
            if (ar.succeeded()) {
                asyncResponse.resume(Response.created(URI.create(createOpportunity.aggregateIdentity().id)).entity(ar.result())
                        .build());
            } else if (ar.cause().getCause() instanceof DomainModelException) {
                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(ar.cause().toString()).build());
            } else {
                asyncResponse.resume(Response.serverError().build());
            }
        });

    }

    @POST
    @Path("/opportunities/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    public void executeCommand(Command command,
                               @PathParam("id") String id,
                               @Suspended final AsyncResponse asyncResponse) {

        LOGGER.info(id);
        domainModel.issueCommand(command).setHandler(ar -> {
            if (ar.succeeded()) {
                asyncResponse.resume(Response.ok(ar.result()).build());
            } else if (ar.cause() instanceof DomainModelException) {
                LOGGER.info(ar.cause().getClass().getName() + "1");
                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(ar.cause().toString()).build());
            } else {
                LOGGER.info(ar.cause().getClass().getName() + "2");
                asyncResponse.resume(Response.serverError().build());
            }
        });

    }


}