package com.membership.users.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.membership.users.domain.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class MetricsInitializer {

    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void initMetricsOnStartup() {
        long usersCount = userRepository.count();
        if (usersCount > 0) {
            Counter.builder("users.created")
                    .description("Nombre d'utilisateurs créés")
                    .tag("type", "user")
                    .register(meterRegistry)
                    .increment((double) usersCount);
        }
    }
}
