package org.athletes.traineatrepeat.util;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TimeProvider {

    private Optional<LocalDate> overriddenDate = Optional.empty();

    public LocalDate getCurrentDate() {
        return overriddenDate.orElse(LocalDate.now());
    }

    public void setCurrentDate(LocalDate date) {
        this.overriddenDate = Optional.of(date);
    }

    public void reset() {
        this.overriddenDate = Optional.empty();
    }
}