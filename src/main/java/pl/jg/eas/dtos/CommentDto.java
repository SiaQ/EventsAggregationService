package pl.jg.eas.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.jg.eas.entities.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentDto {
    private LocalDateTime added;
    private String text;
    private User commentator;
}
