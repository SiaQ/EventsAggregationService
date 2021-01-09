package pl.jg.eas.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String nickname;

    private LocalDateTime created = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_signed_events")
    private List<Event> signedUpForEvents = new ArrayList<>();

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User() {
    }

    public void addRole(Role role) {
        roles.add(role);
    }
    public Set<Role> getRoles() {
        return roles;
    }

    public void signUp(Event event) {
        signedUpForEvents.add(event);
    }

    public List<Event> getSignedUpForEvents() {
        return signedUpForEvents;
    }
}
