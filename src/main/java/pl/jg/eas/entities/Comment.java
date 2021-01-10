package pl.jg.eas.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String commentatorEmail;

    @Column(length = 500)
    private String commentText;

    private LocalDateTime added = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
