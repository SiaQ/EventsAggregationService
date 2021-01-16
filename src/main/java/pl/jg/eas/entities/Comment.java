package pl.jg.eas.entities;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
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

    public void setCommentatorEmail(String commentatorEmail) {
        this.commentatorEmail = commentatorEmail;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
