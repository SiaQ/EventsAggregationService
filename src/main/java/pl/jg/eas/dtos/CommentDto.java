package pl.jg.eas.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentDto {
    private LocalDateTime added;
    private String text;
    private String userNickname;
}
