package io.openinnovationlabs.sales.adapters.http;

import io.openinnovationlabs.ddd.Command;
import io.openinnovationlabs.ddd.DomainModel;
import io.openinnovationlabs.ddd.DomainModelException;
import io.openinnovationlabs.sales.adapters.Translator;
import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.dto.CommandDTO;
import io.openinnovationlabs.sales.dto.CommandProcessingResponseDTO;
import io.openinnovationlabs.sales.dto.CreateOpportunityCommandDTO;
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


@Path("/v1")
@Component
public class OpportunityHttpAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityHttpAdapter.class);

    @Autowired
    private DomainModel domainModel;

    private Translator translator = new Translator();


    @POST
    @Path("/opportunities")
    @Produces("application/json")
    @Consumes("application/json")
    public void createOpportunity(CreateOpportunityCommandDTO createOpportunityCommandDTO,
                                  @Suspended final AsyncResponse asyncResponse) {

        CreateOpportunity createOpportunity = translator.translate(createOpportunityCommandDTO, UUID.randomUUID().toString());
        domainModel.issueCommand(createOpportunity).setHandler(ar -> {
            if (ar.succeeded()) {
                CommandProcessingResponseDTO dto = translator.translate(ar.result());
                asyncResponse.resume(Response.created(URI.create(createOpportunity.aggregateIdentity().id)).entity(dto).build());
            } else if (ar.cause() instanceof DomainModelException) {
                CommandProcessingResponseDTO dto = new CommandProcessingResponseDTO();
                dto.getErrors().add(ar.cause().getLocalizedMessage());
                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(dto).build());
            } else {
                asyncResponse.resume(Response.serverError().build());
            }
        });

    }

    @POST
    @Path("/opportunities/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    public void executeCommand(CommandDTO commandDTO,
                               @PathParam("id") String id,
                               @Suspended final AsyncResponse asyncResponse) {


        Command command = translator.translate(commandDTO, id);
        domainModel.issueCommand(command).setHandler(ar -> {
            if (ar.succeeded()) {
                CommandProcessingResponseDTO dto = translator.translate(ar.result());
                asyncResponse.resume(Response.ok(dto).build());
            } else if (ar.cause() instanceof DomainModelException) {
                CommandProcessingResponseDTO dto = new CommandProcessingResponseDTO();
                dto.getErrors().add(ar.cause().getLocalizedMessage());
                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(dto).build());
            } else {
                asyncResponse.resume(Response.serverError().build());
            }
        });

    }


}