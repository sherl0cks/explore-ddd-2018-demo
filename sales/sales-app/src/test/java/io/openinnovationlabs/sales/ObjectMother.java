package io.openinnovationlabs.sales;

import io.openinnovationlabs.sales.domain.opportunity.CreateOpportunity;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityCreated;
import io.openinnovationlabs.sales.domain.opportunity.OpportunityId;
import io.openinnovationlabs.sales.dto.CommandTypeDTO;
import io.openinnovationlabs.sales.dto.CreateOpportunityCommandDTO;
import io.openinnovationlabs.sales.dto.OpportunityDTO;
import io.openinnovationlabs.sales.dto.WinOpportunityCommandDTO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ObjectMother {

    public static CreateOpportunityCommandDTO createOpportunityCommandDTO() {
        OpportunityDTO opportunityDTO = new OpportunityDTO();
        opportunityDTO.setName("super cool project");
        opportunityDTO.setType("residency");
        opportunityDTO.setCustomerName("acme");
        CreateOpportunityCommandDTO createOpportunityCommandDTO = new CreateOpportunityCommandDTO();
        createOpportunityCommandDTO.setOpportunity(opportunityDTO);
        createOpportunityCommandDTO.setCommandType(CommandTypeDTO.CREATEOPPORTUNITYCOMMAND);
        return createOpportunityCommandDTO;
    }

    public static CreateOpportunityCommandDTO badCreateOpportunityCommandDTO() {
        CreateOpportunityCommandDTO dto = createOpportunityCommandDTO();
        dto.getOpportunity().setName("");
        return dto;
    }

    public static WinOpportunityCommandDTO winOpportunityCommandDTO() {
        WinOpportunityCommandDTO commandDTO = new WinOpportunityCommandDTO();
        commandDTO.setCommandType(CommandTypeDTO.WINOPPORTUNITYCOMMAND);
        return commandDTO;
    }

    public static CreateOpportunity opportunityCommand() {
        return new CreateOpportunity(new
                OpportunityId(UUID.randomUUID().toString()),
                "test", "residency", "super cool opportunity");
    }

    public static OpportunityCreated opportunityCreated() {
        return new OpportunityCreated(new OpportunityId(UUID.randomUUID().toString()), "acme", "residency", Instant
                .now().minus(1, ChronoUnit.HOURS).toString(), 0, "super cool opportunity");
    }

    public static OpportunityCreated badOpportunityCreated() {
        return new OpportunityCreated(new OpportunityId(UUID.randomUUID().toString()), "acme", "residency", Instant
                .now().minus(1, ChronoUnit.HOURS).toString(), 0, "super cool opportunity");
    }


}
