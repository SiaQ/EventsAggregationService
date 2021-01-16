package pl.jg.eas.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 2000, nullable = false)
    private String description;

    @ManyToOne
    @JoinTable(name = "users_events")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_signed_events")
    private final Set<User> signedUpForEvents = new HashSet<>();

    public void signUp(User user) {
        signedUpForEvents.add(user);
    }

    public void signOff(User user) {
        signedUpForEvents.remove(user);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
