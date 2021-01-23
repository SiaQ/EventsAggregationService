package pl.jg.eas.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.entities.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEndDateGreaterThanEqual(LocalDate date, Sort sort);
    List<Event> findByTitleContaining(String eventTitle, Sort sort);
    List<Event> findByTitleContainingAndStartDateAfter(String eventTitle, LocalDate date, Sort sort);
    List<Event> findByTitleContainingAndEndDateGreaterThanEqual(String eventTitle, LocalDate date, Sort sort);
    Optional<Event> findById(Long eventId);
    List<Event> findByUserEmail(String userEmail, Sort sort);
    List<Event> findByStartDateGreaterThanEqual(LocalDate now, Sort sort);
    List<Event> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate after, LocalDate before, Sort sort);
    boolean existsByIdAndUserEmail(Long eventId, String userEmail);
    boolean existsByIdAndSignedUpForEventsEmail(Long eventId, String userEmail);

}
