package io.openinnovationlabs.sales.adapters;

import io.openinnovationlabs.ddd.Command;
import io.openinnovationlabs.ddd.CommandProcessingResponse;
import io.openinnovationlabs.ddd.Event;
import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityCreated;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;
import io.openinnovationlabs.sales.domain.opportunity.WinOpportunity;
import io.openinnovationlabs.sales.dto.CommandDTO;
import io.openinnovationlabs.sales.dto.CommandProcessingResponseDTO;
import io.openinnovationlabs.sales.dto.CreateOpportunityCommandDTO;
import io.openinnovationlabs.sales.dto.OpportunityCreatedEventDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Translator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Translator.class);

    private ModelMapper mapper = new ModelMapper();

    public CreateOpportunity to(CreateOpportunityCommandDTO dto, String id) {
        return new CreateOpportunity(
                new OpportunityId(id),
                dto.getOpportunity().getCustomerName(),
                dto.getOpportunity().getType()
        );

    }

    public Command to(CommandDTO commandDTO, String id) {

        return new WinOpportunity(id);
    }

    public CommandProcessingResponseDTO to(CommandProcessingResponse response) {
        CommandProcessingResponseDTO dtoResponse = new CommandProcessingResponseDTO();
        if (response.events != null && response.events.size() > 0) {
            dtoResponse.setEvents(new ArrayList<>());
            for (Event e : response.events) {
                if (e instanceof OpportunityCreated) {
                    OpportunityCreatedEventDTO dto = to(((OpportunityCreated) e));
                    dtoResponse.getEvents().add(dto);
                    LOGGER.debug(dto.toString());
                } else {
                    LOGGER.error(String.format("Event type %s not currently support in CommandProcessingResponse " +
                            "translation", e.getClass().getName()));
                }
            }
        }
        return dtoResponse;
    }

    public OpportunityCreatedEventDTO to(OpportunityCreated event) {
        return mapper.map(event, OpportunityCreatedEventDTO.class);
    }

}
