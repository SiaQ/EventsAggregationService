package pl.jg.eas.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.jg.eas.entities.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEndDateGreaterThanEqual(LocalDate date, Sort sort);
    List<Event> findByTitleContaining(String title, Sort sort);
    List<Event> findByTitleContainingAndStartDateAfter(String title, LocalDate date, Sort sort);
    List<Event> findByTitleContainingAndEndDateGreaterThanEqual(String title, LocalDate date, Sort sort);
    Optional<Event> findById(Long id);
    List<Event> findByUserEmail(String email, Sort sort);
}
