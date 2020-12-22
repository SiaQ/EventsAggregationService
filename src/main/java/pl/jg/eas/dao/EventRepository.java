package pl.jg.eas.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.dtos.EventShortInfoDto;
import pl.jg.eas.entities.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByEndDateAfter(LocalDate date, Sort sort);
}
