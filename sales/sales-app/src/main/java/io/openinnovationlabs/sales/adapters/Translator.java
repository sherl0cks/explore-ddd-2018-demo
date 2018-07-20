package io.openinnovationlabs.sales.adapters;

import io.openinnovationlabs.ddd.Command;
import io.openinnovationlabs.ddd.CommandProcessingResponse;
import io.openinnovationlabs.ddd.Event;
import io.openinnovationlabs.sales.domain.opportunity.*;
import io.openinnovationlabs.sales.dto.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Translator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Translator.class);

    private ModelMapper mapper = new ModelMapper();

    public CreateOpportunity translate(CreateOpportunityCommandDTO dto, String id) {
        return new CreateOpportunity(
                new OpportunityId(id),
                dto.getOpportunity().getCustomerName(),
                dto.getOpportunity().getType(),
                dto.getOpportunity().getName()
        );

    }

    public Command translate(CommandDTO commandDTO, String id) {

        return new WinOpportunity(id);
    }

    public CommandProcessingResponseDTO translate(CommandProcessingResponse response) {
        CommandProcessingResponseDTO dtoResponse = new CommandProcessingResponseDTO();
        if (response.events != null && response.events.size() > 0) {
            for (Event e : response.events) {
                if (e instanceof OpportunityCreated) {
                    dtoResponse.getEvents().add(translate(((OpportunityCreated) e)));
                } else if (e instanceof OpportunityWon) {
                    dtoResponse.getEvents().add(translate((OpportunityWon) e));
                } else {
                    LOGGER.error(String.format("Event type %s not currently support in CommandProcessingResponse " +
                            "translation", e.getClass().getName()));
                }
            }
        }
        LOGGER.debug(dtoResponse.toString());
        return dtoResponse;
    }

    public OpportunityCreatedEventDTO translate(OpportunityCreated event) {
        OpportunityCreatedEventDTO dto = mapper.map(event, OpportunityCreatedEventDTO.class);
        mapCommonEventProperties(event, dto);
        dto.setEventType(EventTypeDTO.OPPORTUNITYCREATEDEVENT);
        return dto;
    }

    public OpportunityWonEventDTO translate(OpportunityWon event) {
        OpportunityWonEventDTO dto = mapper.map(event, OpportunityWonEventDTO.class);
        mapCommonEventProperties(event, dto);
        dto.setEventType(EventTypeDTO.OPPORTUNITYWONEVENT);
        return dto;
    }

    private void mapCommonEventProperties(Event e, EventDTO dto) {
        dto.setOccurredOn(e.getOccurredOn().toString());
        AggregateIdentityDTO id = new AggregateIdentityDTO();
        id.setType(AggregateTypeDTO.OPPORTUNITY);
        id.setUid(e.getAggregateIdentity().id);
        dto.setAggregateIdentity(id);
    }
}
