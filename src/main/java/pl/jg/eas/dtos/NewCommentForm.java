package pl.jg.eas.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class NewCommentForm {

    @Size(min = 1, max = 500, message = "Comment cannot be empty and size max 500")
    private String comment;

    @Override
    public String toString() {
        return "NewCommentForm{" +
                "comment='" + comment + '\'' +
                '}';
    }
}
