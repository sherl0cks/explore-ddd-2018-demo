package io.cabit;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredOn();
}
