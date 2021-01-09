package pl.jg.eas.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private long id;

    @Column(length = 100)
    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinTable(name = "users_events")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_signed_events")
    private Set<User> signedUpForEvents = new HashSet<>();

    public void signUp(User user) {
        signedUpForEvents.add(user);
    }

    public void signOff(User user) {
        signedUpForEvents.remove(user);
    }

    public Set<User> getSignedUpForEvents() {
        return signedUpForEvents;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }
}
