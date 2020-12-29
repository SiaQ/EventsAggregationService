package pl.jg.eas.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
public class NewCommentForm {

    @Size(max = 500, message = "Comment size max 500")
    private String comment;

    @Override
    public String toString() {
        return "NewCommentForm{" +
                "comment='" + comment + '\'' +
                '}';
    }
}
